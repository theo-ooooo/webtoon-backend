package com.webtoon.domain.readhistory.controller;

import com.webtoon.domain.readhistory.dto.response.ReadHistoryResponse;
import com.webtoon.domain.readhistory.service.ReadHistoryService;
import com.webtoon.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/read-history")
@RequiredArgsConstructor
public class ReadHistoryController {

    private final ReadHistoryService readHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadHistoryResponse>>> getReadHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(readHistoryService.getReadHistory(userId)));
    }
}
