package com.webtoon.domain.readhistory.entity;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "read_histories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "comic_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_episode_id", nullable = false)
    private Episode lastEpisode;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public ReadHistory(User user, Comic comic, Episode lastEpisode) {
        this.user = user;
        this.comic = comic;
        this.lastEpisode = lastEpisode;
    }

    public void updateLastEpisode(Episode episode) {
        this.lastEpisode = episode;
    }
}
