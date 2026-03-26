package com.webtoon.domain.genre.controller;

import com.webtoon.domain.genre.dto.response.GenreResponse;
import com.webtoon.domain.genre.service.GenreService;
import com.webtoon.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(genreService.findAll()));
    }
}
