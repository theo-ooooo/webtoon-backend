package com.webtoon.domain.episode.service;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.comic.repository.ComicRepository;
import com.webtoon.domain.episode.dto.request.EpisodeCreateRequest;
import com.webtoon.domain.episode.dto.request.EpisodeUpdateRequest;
import com.webtoon.domain.episode.dto.response.EpisodeDetailResponse;
import com.webtoon.domain.episode.dto.response.EpisodeListResponse;
import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.episode.entity.EpisodeImage;
import com.webtoon.domain.episode.entity.EpisodeView;
import com.webtoon.domain.episode.repository.EpisodeImageRepository;
import com.webtoon.domain.episode.repository.EpisodeRepository;
import com.webtoon.domain.episode.repository.EpisodeViewRepository;
import com.webtoon.domain.purchase.repository.PurchaseRepository;
import com.webtoon.domain.readhistory.entity.ReadHistory;
import com.webtoon.domain.readhistory.repository.ReadHistoryRepository;
import com.webtoon.domain.user.entity.User;
import com.webtoon.domain.user.repository.UserRepository;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeImageRepository episodeImageRepository;
    private final EpisodeViewRepository episodeViewRepository;
    private final ComicRepository comicRepository;
    private final PurchaseRepository purchaseRepository;
    private final ReadHistoryRepository readHistoryRepository;
    private final UserRepository userRepository;

    public List<EpisodeListResponse> findByComicId(Long comicId, Long userId) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMIC_NOT_FOUND));

        List<Episode> episodes = episodeRepository.findByComicIdOrderByEpisodeNumberAsc(comicId);

        Set<Long> purchasedEpisodeIds = Collections.emptySet();
        Set<Long> readEpisodeIds = Collections.emptySet();

        if (userId != null) {
            List<Long> episodeIds = episodes.stream().map(Episode::getId).toList();
            purchasedEpisodeIds = new HashSet<>(purchaseRepository.findPurchasedEpisodeIds(userId, episodeIds));

            // 읽은 에피소드: ReadHistory에서 마지막 본 에피소드 번호 이하
            readHistoryRepository.findByUserIdAndComicId(userId, comicId)
                    .ifPresent(rh -> {});
            // 간단하게 구매+무료 에피소드 중 열람 로그가 있는 것으로 판단
        }

        Set<Long> finalPurchased = purchasedEpisodeIds;
        return episodes.stream()
                .map(ep -> EpisodeListResponse.of(
                        ep, comic.getFreeEpisodeCount(),
                        finalPurchased.contains(ep.getId()),
                        false // 열람 여부는 별도 조회 필요 시 확장
                ))
                .toList();
    }

    @Transactional
    public EpisodeDetailResponse findById(Long comicId, Long episodeId, Long userId) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMIC_NOT_FOUND));

        Episode episode = episodeRepository.findByComicIdAndId(comicId, episodeId)
                .orElseThrow(() -> new ApiException(ErrorCode.EPISODE_NOT_FOUND));

        boolean isFree = episode.isFree(comic.getFreeEpisodeCount());
        boolean purchased = false;

        if (userId != null) {
            purchased = purchaseRepository.existsByUserIdAndEpisodeId(userId, episodeId);
        }

        // 유료인데 미구매면 이미지 비공개
        if (!isFree && !purchased) {
            throw new ApiException(ErrorCode.EPISODE_NOT_PURCHASED);
        }

        List<EpisodeImage> images = episodeImageRepository.findByEpisodeIdOrderByOrderAsc(episodeId);

        // 이전/다음 에피소드
        List<Episode> allEpisodes = episodeRepository.findByComicIdOrderByEpisodeNumberAsc(comicId);
        Long prevId = null, nextId = null;
        for (int i = 0; i < allEpisodes.size(); i++) {
            if (allEpisodes.get(i).getId().equals(episodeId)) {
                if (i > 0) prevId = allEpisodes.get(i - 1).getId();
                if (i < allEpisodes.size() - 1) nextId = allEpisodes.get(i + 1).getId();
                break;
            }
        }

        // 조회 로그 기록
        if (userId != null) {
            recordView(episode, userId);
            updateReadHistory(userId, comic, episode);
        }

        return EpisodeDetailResponse.of(episode, comic.getFreeEpisodeCount(), purchased, images, prevId, nextId);
    }

    @Transactional
    public void recordView(Episode episode, Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        episodeViewRepository.save(EpisodeView.builder().episode(episode).user(user).build());
    }

    @Transactional
    public void updateReadHistory(Long userId, Comic comic, Episode episode) {
        ReadHistory history = readHistoryRepository.findByUserIdAndComicId(userId, comic.getId())
                .orElse(null);

        if (history != null) {
            history.updateLastEpisode(episode);
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            readHistoryRepository.save(ReadHistory.builder()
                    .user(user).comic(comic).lastEpisode(episode).build());
        }
    }

    @Transactional
    public Long create(Long comicId, EpisodeCreateRequest request) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMIC_NOT_FOUND));

        Episode episode = Episode.builder()
                .comic(comic)
                .episodeNumber(request.getEpisodeNumber())
                .title(request.getTitle())
                .thumbnail(request.getThumbnail())
                .coinPrice(request.getCoinPrice())
                .build();

        episodeRepository.save(episode);

        if (request.getImageUrls() != null) {
            saveImages(episode, request.getImageUrls());
        }

        return episode.getId();
    }

    @Transactional
    public void update(Long comicId, Long episodeId, EpisodeUpdateRequest request) {
        Episode episode = episodeRepository.findByComicIdAndId(comicId, episodeId)
                .orElseThrow(() -> new ApiException(ErrorCode.EPISODE_NOT_FOUND));

        episode.update(request.getTitle(), request.getThumbnail(), request.getCoinPrice());

        if (request.getImageUrls() != null) {
            episodeImageRepository.deleteByEpisodeId(episodeId);
            saveImages(episode, request.getImageUrls());
        }
    }

    @Transactional
    public void delete(Long comicId, Long episodeId) {
        Episode episode = episodeRepository.findByComicIdAndId(comicId, episodeId)
                .orElseThrow(() -> new ApiException(ErrorCode.EPISODE_NOT_FOUND));
        episodeImageRepository.deleteByEpisodeId(episodeId);
        episodeRepository.delete(episode);
    }

    private void saveImages(Episode episode, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            episodeImageRepository.save(
                    EpisodeImage.builder()
                            .episode(episode)
                            .imageUrl(imageUrls.get(i))
                            .order(i + 1)
                            .build()
            );
        }
    }
}
