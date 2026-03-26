package com.webtoon.domain.episode.dto.response;

import com.webtoon.domain.episode.entity.Episode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class EpisodeListResponse {
    private Long id;
    private Integer episodeNumber;
    private String title;
    private String thumbnail;
    private Integer coinPrice;
    private boolean free;
    private boolean purchased;
    private boolean read;
    private LocalDateTime createdAt;

    public static EpisodeListResponse of(Episode episode, int freeEpisodeCount,
                                         boolean purchased, boolean read) {
        return EpisodeListResponse.builder()
                .id(episode.getId())
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .thumbnail(episode.getThumbnail())
                .coinPrice(episode.getCoinPrice())
                .free(episode.isFree(freeEpisodeCount))
                .purchased(purchased)
                .read(read)
                .createdAt(episode.getCreatedAt())
                .build();
    }
}
