package com.webtoon.domain.genre.service;

import com.webtoon.domain.genre.dto.response.GenreResponse;
import com.webtoon.domain.genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreResponse> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreResponse::from)
                .toList();
    }
}
