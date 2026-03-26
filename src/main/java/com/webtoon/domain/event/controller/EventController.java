package com.webtoon.domain.event.controller;

import com.webtoon.domain.event.dto.request.EventCreateRequest;
import com.webtoon.domain.event.dto.response.EventResponse;
import com.webtoon.domain.event.service.EventService;
import com.webtoon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(eventService.findActiveEvents()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(@Valid @RequestBody EventCreateRequest request) {
        Long id = eventService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(Map.of("id", id)));
    }
}
