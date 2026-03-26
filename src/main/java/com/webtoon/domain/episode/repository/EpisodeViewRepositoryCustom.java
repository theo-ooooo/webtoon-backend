package com.webtoon.domain.episode.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface EpisodeViewRepositoryCustom {
    List<Object[]> countByComicSince(LocalDateTime since);
    List<Object[]> countByComic();
}
