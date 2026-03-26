package com.webtoon.domain.purchase.controller;

import com.webtoon.domain.purchase.dto.response.PurchaseResponse;
import com.webtoon.domain.purchase.service.PurchaseService;
import com.webtoon.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/api/comics/{comicId}/episodes/{episodeId}/purchase")
    public ResponseEntity<ApiResponse<Void>> purchase(
            Authentication authentication,
            @PathVariable Long comicId, @PathVariable Long episodeId) {
        Long userId = (Long) authentication.getPrincipal();
        purchaseService.purchase(userId, comicId, episodeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
    }

    @GetMapping("/api/purchases")
    public ResponseEntity<ApiResponse<Page<PurchaseResponse>>> myPurchases(
            Authentication authentication, Pageable pageable) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(purchaseService.getMyPurchases(userId, pageable)));
    }
}
