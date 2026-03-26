package com.webtoon.domain.rating.entity;

import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.user.entity.User;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "episode_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Rating(User user, Episode episode, Integer score) {
        validateScore(score);
        this.user = user;
        this.episode = episode;
        this.score = score;
    }

    public void updateScore(Integer score) {
        validateScore(score);
        this.score = score;
    }

    private void validateScore(Integer score) {
        if (score < 1 || score > 5) {
            throw new ApiException(ErrorCode.INVALID_SCORE);
        }
    }
}
