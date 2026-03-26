package com.webtoon.domain.rating.service;

import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.episode.repository.EpisodeRepository;
import com.webtoon.domain.rating.dto.request.RatingRequest;
import com.webtoon.domain.rating.dto.response.RatingResponse;
import com.webtoon.domain.rating.entity.Rating;
import com.webtoon.domain.rating.repository.RatingRepository;
import com.webtoon.domain.user.entity.User;
import com.webtoon.domain.user.repository.UserRepository;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final EpisodeRepository episodeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void rate(Long userId, Long comicId, Long episodeId, RatingRequest request) {
        Episode episode = episodeRepository.findByComicIdAndId(comicId, episodeId)
                .orElseThrow(() -> new ApiException(ErrorCode.EPISODE_NOT_FOUND));

        Optional<Rating> existing = ratingRepository.findByUserIdAndEpisodeId(userId, episodeId);

        if (existing.isPresent()) {
            existing.get().updateScore(request.getScore());
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
            ratingRepository.save(Rating.builder()
                    .user(user).episode(episode).score(request.getScore()).build());
        }
    }

    public RatingResponse getMyRating(Long userId, Long comicId, Long episodeId) {
        return ratingRepository.findByUserIdAndEpisodeId(userId, episodeId)
                .map(r -> new RatingResponse(r.getScore()))
                .orElse(new RatingResponse(null));
    }
}
