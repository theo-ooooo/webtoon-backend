package com.webtoon.domain.comic.repository;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComicRepositoryCustom {
    Page<Comic> findByDay(DayOfWeek day, Pageable pageable);
    Page<Comic> findByStatus(ComicStatus status, Pageable pageable);
    Page<Comic> findByGenreId(Long genreId, Pageable pageable);
    Page<Comic> search(String keyword, Pageable pageable);
}
