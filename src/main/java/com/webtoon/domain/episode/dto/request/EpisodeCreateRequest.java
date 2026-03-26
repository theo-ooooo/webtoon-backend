package com.webtoon.domain.episode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class EpisodeCreateRequest {

    @NotNull(message = "회차 번호는 필수입니다.")
    private Integer episodeNumber;

    @NotBlank(message = "에피소드 제목은 필수입니다.")
    private String title;

    private String thumbnail;
    private Integer coinPrice;
    private List<String> imageUrls;
}
