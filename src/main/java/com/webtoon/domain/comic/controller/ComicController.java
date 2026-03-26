package com.webtoon.domain.comic.controller;

import com.webtoon.domain.comic.dto.request.ComicCreateRequest;
import com.webtoon.domain.comic.dto.request.ComicUpdateRequest;
import com.webtoon.domain.comic.dto.response.ComicDetailResponse;
import com.webtoon.domain.comic.dto.response.ComicListResponse;
import com.webtoon.domain.comic.service.ComicService;
import com.webtoon.global.enums.DayOfWeek;
import com.webtoon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comics")
@RequiredArgsConstructor
public class ComicController {

    private final ComicService comicService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ComicListResponse>>> list(
            @RequestParam(required = false) DayOfWeek day,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "false") boolean completed,
            Pageable pageable) {

        Page<ComicListResponse> result;

        if (keyword != null && !keyword.isBlank()) {
            result = comicService.search(keyword, pageable);
        } else if (completed) {
            result = comicService.findCompleted(pageable);
        } else if (genreId != null) {
            result = comicService.findByGenre(genreId, pageable);
        } else if (day != null) {
            result = comicService.findByDay(day, pageable);
        } else {
            result = comicService.findByDay(currentDay(), pageable);
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComicDetailResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(comicService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(@Valid @RequestBody ComicCreateRequest request) {
        Long id = comicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(Map.of("id", id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable Long id,
                                       @Valid @RequestBody ComicUpdateRequest request) {
        comicService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        comicService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    private DayOfWeek currentDay() {
        return DayOfWeek.valueOf(
                java.time.LocalDate.now().getDayOfWeek().name().substring(0, 3)
        );
    }
}
