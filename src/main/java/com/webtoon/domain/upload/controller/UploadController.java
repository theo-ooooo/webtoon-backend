package com.webtoon.domain.upload.controller;

import com.webtoon.domain.upload.service.S3UploadService;
import com.webtoon.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final S3UploadService s3UploadService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> upload(
            @RequestParam("files") List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(s3UploadService.upload(file));
        }
        return ResponseEntity.ok(ApiResponse.ok(Map.of("urls", urls)));
    }
}
