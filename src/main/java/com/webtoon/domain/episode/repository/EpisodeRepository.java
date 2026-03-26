package com.webtoon.domain.episode.repository;

import com.webtoon.domain.episode.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    List<Episode> findByComicIdOrderByEpisodeNumberAsc(Long comicId);
    Optional<Episode> findByComicIdAndEpisodeNumber(Long comicId, Integer episodeNumber);
    Optional<Episode> findByComicIdAndId(Long comicId, Long episodeId);
}
