package com.webtoon.domain.ranking.entity;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.global.enums.RankingPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "popular_rankings", indexes = {
        @Index(name = "idx_popular_rankings_period", columnList = "period, ranking")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic;

    @Column(name = "ranking", nullable = false)
    private Integer rank;

    @Column(nullable = false)
    private Long viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankingPeriod period;

    @Column(nullable = false, updatable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        this.calculatedAt = LocalDateTime.now();
    }

    @Builder
    public PopularRanking(Comic comic, Integer rank, Long viewCount, RankingPeriod period) {
        this.comic = comic;
        this.rank = rank;
        this.viewCount = viewCount;
        this.period = period;
    }
}
