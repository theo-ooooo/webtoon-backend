package com.webtoon.domain.episode.entity;

import com.webtoon.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "episode_views", indexes = {
        @Index(name = "idx_episode_views_viewed_at", columnList = "viewedAt"),
        @Index(name = "idx_episode_views_episode_id", columnList = "episode_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        this.viewedAt = LocalDateTime.now();
    }

    @Builder
    public EpisodeView(Episode episode, User user) {
        this.episode = episode;
        this.user = user;
    }
}
