package com.webtoon.domain.comic.dto.response;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class ComicListResponse {
    private Long id;
    private String title;
    private String author;
    private String thumbnail;
    private Long viewCount;
    private BigDecimal averageRating;
    private ComicStatus status;
    private Set<DayOfWeek> days;
    private Set<String> genres;

    public static ComicListResponse from(Comic comic) {
        return ComicListResponse.builder()
                .id(comic.getId())
                .title(comic.getTitle())
                .author(comic.getAuthor())
                .thumbnail(comic.getThumbnail())
                .viewCount(comic.getViewCount())
                .averageRating(comic.getAverageRating())
                .status(comic.getStatus())
                .days(comic.getDays())
                .genres(comic.getGenres().stream()
                        .map(g -> g.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
