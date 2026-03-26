package com.webtoon.domain.genre.dto.response;

import com.webtoon.domain.genre.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenreResponse {
    private Long id;
    private String name;

    public static GenreResponse from(Genre genre) {
        return new GenreResponse(genre.getId(), genre.getName());
    }
}
