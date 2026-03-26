package com.webtoon.domain.readhistory.dto.response;

import com.webtoon.domain.readhistory.entity.ReadHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReadHistoryResponse {
    private Long comicId;
    private String comicTitle;
    private String comicThumbnail;
    private Long lastEpisodeId;
    private Integer lastEpisodeNumber;
    private String lastEpisodeTitle;
    private LocalDateTime updatedAt;

    public static ReadHistoryResponse from(ReadHistory history) {
        return new ReadHistoryResponse(
                history.getComic().getId(),
                history.getComic().getTitle(),
                history.getComic().getThumbnail(),
                history.getLastEpisode().getId(),
                history.getLastEpisode().getEpisodeNumber(),
                history.getLastEpisode().getTitle(),
                history.getUpdatedAt()
        );
    }
}
