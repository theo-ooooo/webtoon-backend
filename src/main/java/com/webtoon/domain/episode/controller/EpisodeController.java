package com.webtoon.domain.episode.controller;

import com.webtoon.domain.episode.dto.request.EpisodeCreateRequest;
import com.webtoon.domain.episode.dto.request.EpisodeUpdateRequest;
import com.webtoon.domain.episode.dto.response.EpisodeDetailResponse;
import com.webtoon.domain.episode.dto.response.EpisodeListResponse;
import com.webtoon.domain.episode.service.EpisodeService;
import com.webtoon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comics/{comicId}/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EpisodeListResponse>>> list(
            @PathVariable Long comicId, Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        return ResponseEntity.ok(ApiResponse.ok(episodeService.findByComicId(comicId, userId)));
    }

    @GetMapping("/{episodeId}")
    public ResponseEntity<ApiResponse<EpisodeDetailResponse>> detail(
            @PathVariable Long comicId, @PathVariable Long episodeId,
            Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        return ResponseEntity.ok(ApiResponse.ok(episodeService.findById(comicId, episodeId, userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(
            @PathVariable Long comicId, @Valid @RequestBody EpisodeCreateRequest request) {
        Long id = episodeService.create(comicId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(Map.of("id", id)));
    }

    @PutMapping("/{episodeId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long comicId, @PathVariable Long episodeId,
            @Valid @RequestBody EpisodeUpdateRequest request) {
        episodeService.update(comicId, episodeId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{episodeId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long comicId, @PathVariable Long episodeId) {
        episodeService.delete(comicId, episodeId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
