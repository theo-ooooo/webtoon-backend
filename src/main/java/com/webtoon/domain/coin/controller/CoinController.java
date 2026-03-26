package com.webtoon.domain.coin.controller;

import com.webtoon.domain.coin.dto.request.CoinChargeRequest;
import com.webtoon.domain.coin.dto.response.CoinTransactionResponse;
import com.webtoon.domain.coin.service.CoinService;
import com.webtoon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> charge(
            Authentication authentication, @Valid @RequestBody CoinChargeRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Integer balance = coinService.charge(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("coinBalance", balance)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<CoinTransactionResponse>>> transactions(
            Authentication authentication, Pageable pageable) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(coinService.getTransactions(userId, pageable)));
    }
}
