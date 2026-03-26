package com.webtoon.domain.ranking.controller;

import com.webtoon.domain.ranking.dto.response.PopularRankingResponse;
import com.webtoon.domain.ranking.service.RankingService;
import com.webtoon.global.enums.RankingPeriod;
import com.webtoon.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comics/popular")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PopularRankingResponse>>> popular(
            @RequestParam(defaultValue = "DAILY") RankingPeriod period) {
        return ResponseEntity.ok(ApiResponse.ok(rankingService.getRanking(period)));
    }
}
