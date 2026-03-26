package com.webtoon.domain.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EventCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    private String imageUrl;

    private String linkUrl;

    private String bgColor;

    private LocalDate startDate;

    private LocalDate endDate;
}
