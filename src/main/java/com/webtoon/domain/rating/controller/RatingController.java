package com.webtoon.domain.rating.controller;

import com.webtoon.domain.rating.dto.request.RatingRequest;
import com.webtoon.domain.rating.dto.response.RatingResponse;
import com.webtoon.domain.rating.service.RatingService;
import com.webtoon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comics/{comicId}/episodes/{episodeId}/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> rate(
            Authentication authentication,
            @PathVariable Long comicId, @PathVariable Long episodeId,
            @Valid @RequestBody RatingRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        ratingService.rate(userId, comicId, episodeId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<RatingResponse>> getMyRating(
            Authentication authentication,
            @PathVariable Long comicId, @PathVariable Long episodeId) {
        if (authentication == null) {
            return ResponseEntity.ok(ApiResponse.ok(new RatingResponse(null)));
        }
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(ratingService.getMyRating(userId, comicId, episodeId)));
    }
}
