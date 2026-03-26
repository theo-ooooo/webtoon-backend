package com.webtoon.domain.episode.repository;

import com.webtoon.domain.episode.entity.EpisodeView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeViewRepository extends JpaRepository<EpisodeView, Long>, EpisodeViewRepositoryCustom {
}
