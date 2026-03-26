package com.webtoon.domain.comic.repository;

import com.webtoon.domain.comic.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicRepository extends JpaRepository<Comic, Long>, ComicRepositoryCustom {
}
