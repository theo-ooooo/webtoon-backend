package com.webtoon.domain.comic.dto.response;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class ComicDetailResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String thumbnail;
    private Long viewCount;
    private BigDecimal averageRating;
    private Integer freeEpisodeCount;
    private ComicStatus status;
    private Set<DayOfWeek> days;
    private Set<String> genres;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ComicDetailResponse from(Comic comic) {
        return ComicDetailResponse.builder()
                .id(comic.getId())
                .title(comic.getTitle())
                .author(comic.getAuthor())
                .description(comic.getDescription())
                .thumbnail(comic.getThumbnail())
                .viewCount(comic.getViewCount())
                .averageRating(comic.getAverageRating())
                .freeEpisodeCount(comic.getFreeEpisodeCount())
                .status(comic.getStatus())
                .days(comic.getDays())
                .genres(comic.getGenres().stream()
                        .map(g -> g.getName())
                        .collect(Collectors.toSet()))
                .createdAt(comic.getCreatedAt())
                .updatedAt(comic.getUpdatedAt())
                .build();
    }
}
