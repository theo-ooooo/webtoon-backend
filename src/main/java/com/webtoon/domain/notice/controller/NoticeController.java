package com.webtoon.domain.notice.controller;

import com.webtoon.domain.notice.dto.request.NoticeCreateRequest;
import com.webtoon.domain.notice.dto.response.NoticeDetailResponse;
import com.webtoon.domain.notice.dto.response.NoticeListResponse;
import com.webtoon.domain.notice.service.NoticeService;
import com.webtoon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeListResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(@Valid @RequestBody NoticeCreateRequest request) {
        Long id = noticeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(Map.of("id", id)));
    }
}
