package com.webtoon.domain.rating.repository;

import com.webtoon.domain.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long>, RatingRepositoryCustom {
    Optional<Rating> findByUserIdAndEpisodeId(Long userId, Long episodeId);
}
