package com.webtoon.domain.purchase.service;

import com.webtoon.domain.coin.entity.CoinTransaction;
import com.webtoon.domain.coin.repository.CoinTransactionRepository;
import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.comic.repository.ComicRepository;
import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.episode.repository.EpisodeRepository;
import com.webtoon.domain.purchase.dto.response.PurchaseResponse;
import com.webtoon.domain.purchase.entity.Purchase;
import com.webtoon.domain.purchase.repository.PurchaseRepository;
import com.webtoon.domain.user.entity.User;
import com.webtoon.domain.user.repository.UserRepository;
import com.webtoon.global.enums.CoinTransactionType;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final EpisodeRepository episodeRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;
    private final CoinTransactionRepository coinTransactionRepository;

    @Transactional
    public void purchase(Long userId, Long comicId, Long episodeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMIC_NOT_FOUND));

        Episode episode = episodeRepository.findByComicIdAndId(comicId, episodeId)
                .orElseThrow(() -> new ApiException(ErrorCode.EPISODE_NOT_FOUND));

        if (episode.isFree(comic.getFreeEpisodeCount())) {
            throw new ApiException(ErrorCode.FREE_EPISODE);
        }

        if (purchaseRepository.existsByUserIdAndEpisodeId(userId, episodeId)) {
            throw new ApiException(ErrorCode.ALREADY_PURCHASED);
        }

        user.useCoin(episode.getCoinPrice());

        Purchase purchase = Purchase.builder()
                .user(user)
                .episode(episode)
                .coinUsed(episode.getCoinPrice())
                .build();
        purchaseRepository.save(purchase);

        coinTransactionRepository.save(CoinTransaction.builder()
                .user(user)
                .type(CoinTransactionType.USE)
                .amount(episode.getCoinPrice())
                .reason(comic.getTitle() + " " + episode.getEpisodeNumber() + "화 구매")
                .relatedPurchase(purchase)
                .build());
    }

    public Page<PurchaseResponse> getMyPurchases(Long userId, Pageable pageable) {
        return purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId, pageable)
                .map(PurchaseResponse::from);
    }
}
