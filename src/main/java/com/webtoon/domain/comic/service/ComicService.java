package com.webtoon.domain.comic.service;

import com.webtoon.domain.comic.dto.request.ComicCreateRequest;
import com.webtoon.domain.comic.dto.request.ComicUpdateRequest;
import com.webtoon.domain.comic.dto.response.ComicDetailResponse;
import com.webtoon.domain.comic.dto.response.ComicListResponse;
import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.comic.repository.ComicRepository;
import com.webtoon.domain.genre.entity.Genre;
import com.webtoon.domain.genre.repository.GenreRepository;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComicService {

    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;

    public Page<ComicListResponse> findByDay(DayOfWeek day, Pageable pageable) {
        return comicRepository.findByDay(day, pageable).map(ComicListResponse::from);
    }

    public Page<ComicListResponse> findCompleted(Pageable pageable) {
        return comicRepository.findByStatus(ComicStatus.COMPLETED, pageable).map(ComicListResponse::from);
    }

    public Page<ComicListResponse> findByGenre(Long genreId, Pageable pageable) {
        return comicRepository.findByGenreId(genreId, pageable).map(ComicListResponse::from);
    }

    public Page<ComicListResponse> search(String keyword, Pageable pageable) {
        return comicRepository.search(keyword, pageable).map(ComicListResponse::from);
    }

    public ComicDetailResponse findById(Long id) {
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMIC_NOT_FOUND));
        return ComicDetailResponse.from(comic);
    }

    @Transactional
    public Long create(ComicCreateRequest request) {
        Comic comic = Comic.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .freeEpisodeCount(request.getFreeEpisodeCount())
                .status(request.getStatus())
                .build();

        if (request.getDays() != null) {
            comic.updateDays(request.getDays());
        }
        if (request.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            comic.updateGenres(genres);
        }

        return comicRepository.save(comic).getId();
    }

    @Transactional
    public void update(Long id, ComicUpdateRequest request) {
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COMIC_NOT_FOUND));

        comic.update(
                request.getTitle(), request.getAuthor(), request.getDescription(),
                request.getThumbnail(), request.getFreeEpisodeCount(), request.getStatus()
        );

        if (request.getDays() != null) {
            comic.updateDays(request.getDays());
        }
        if (request.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            comic.updateGenres(genres);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!comicRepository.existsById(id)) {
            throw new ApiException(ErrorCode.COMIC_NOT_FOUND);
        }
        comicRepository.deleteById(id);
    }
}
