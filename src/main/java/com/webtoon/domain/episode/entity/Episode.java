package com.webtoon.domain.episode.entity;

import com.webtoon.domain.comic.entity.Comic;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "episodes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comic_id", "episode_number"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(nullable = false)
    private String title;

    private String thumbnail;

    @Column(nullable = false)
    private Integer coinPrice = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Episode(Comic comic, Integer episodeNumber, String title, String thumbnail, Integer coinPrice) {
        this.comic = comic;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.thumbnail = thumbnail;
        this.coinPrice = coinPrice != null ? coinPrice : 0;
    }

    public void update(String title, String thumbnail, Integer coinPrice) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.coinPrice = coinPrice;
    }

    public boolean isFree(int freeEpisodeCount) {
        return this.episodeNumber <= freeEpisodeCount || this.coinPrice == 0;
    }
}
