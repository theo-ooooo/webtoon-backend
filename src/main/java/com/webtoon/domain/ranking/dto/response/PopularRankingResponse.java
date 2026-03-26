package com.webtoon.domain.ranking.dto.response;

import com.webtoon.domain.ranking.entity.PopularRanking;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularRankingResponse {
    private Integer rank;
    private Long comicId;
    private String title;
    private String author;
    private String thumbnail;
    private Long viewCount;

    public static PopularRankingResponse from(PopularRanking ranking) {
        return new PopularRankingResponse(
                ranking.getRank(),
                ranking.getComic().getId(),
                ranking.getComic().getTitle(),
                ranking.getComic().getAuthor(),
                ranking.getComic().getThumbnail(),
                ranking.getViewCount()
        );
    }
}
