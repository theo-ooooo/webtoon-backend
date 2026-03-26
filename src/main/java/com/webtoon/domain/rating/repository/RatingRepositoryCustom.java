package com.webtoon.domain.rating.repository;

import java.util.List;

public interface RatingRepositoryCustom {
    List<Object[]> averageScoreByComic();
    Double averageScoreByComicId(Long comicId);
}
