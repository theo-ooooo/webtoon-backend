package com.webtoon.domain.comic.dto.request;

import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Set;

@Getter
public class ComicUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "작가명은 필수입니다.")
    private String author;

    private String description;
    private String thumbnail;
    private Integer freeEpisodeCount;

    @NotNull(message = "연재 상태는 필수입니다.")
    private ComicStatus status;

    private Set<DayOfWeek> days;
    private Set<Long> genreIds;
}
