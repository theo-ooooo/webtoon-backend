package com.webtoon.domain.purchase.entity;

import com.webtoon.domain.episode.entity.Episode;
import com.webtoon.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchases", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "episode_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {

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
    private Integer coinUsed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    @PrePersist
    protected void onCreate() {
        this.purchasedAt = LocalDateTime.now();
    }

    @Builder
    public Purchase(User user, Episode episode, Integer coinUsed) {
        this.user = user;
        this.episode = episode;
        this.coinUsed = coinUsed;
    }
}
