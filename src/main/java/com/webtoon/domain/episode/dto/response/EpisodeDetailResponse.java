package com.webtoon.domain.episode.dto.response;

import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.episode.entity.EpisodeImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class EpisodeDetailResponse {
    private Long id;
    private Long comicId;
    private Integer episodeNumber;
    private String title;
    private boolean free;
    private boolean purchased;
    private List<String> imageUrls;
    private Long prevEpisodeId;
    private Long nextEpisodeId;

    public static EpisodeDetailResponse of(Episode episode, int freeEpisodeCount,
                                           boolean purchased, List<EpisodeImage> images,
                                           Long prevId, Long nextId) {
        return EpisodeDetailResponse.builder()
                .id(episode.getId())
                .comicId(episode.getComic().getId())
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .free(episode.isFree(freeEpisodeCount))
                .purchased(purchased)
                .imageUrls(images.stream().map(EpisodeImage::getImageUrl).toList())
                .prevEpisodeId(prevId)
                .nextEpisodeId(nextId)
                .build();
    }
}
