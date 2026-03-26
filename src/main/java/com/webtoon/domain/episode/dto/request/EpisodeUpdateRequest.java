package com.webtoon.domain.episode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class EpisodeUpdateRequest {

    @NotBlank(message = "에피소드 제목은 필수입니다.")
    private String title;

    private String thumbnail;
    private Integer coinPrice;
    private List<String> imageUrls;
}
