package com.webtoon.domain.episode.repository;

import com.webtoon.domain.episode.entity.EpisodeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeImageRepository extends JpaRepository<EpisodeImage, Long> {
    List<EpisodeImage> findByEpisodeIdOrderByOrderAsc(Long episodeId);
    void deleteByEpisodeId(Long episodeId);
}
