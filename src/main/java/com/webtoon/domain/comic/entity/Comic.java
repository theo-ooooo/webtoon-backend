package com.webtoon.domain.comic.entity;

import com.webtoon.domain.genre.entity.Genre;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(precision = 3, scale = 1)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer freeEpisodeCount = 3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComicStatus status = ComicStatus.ONGOING;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "comic_days", joinColumns = @JoinColumn(name = "comic_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private Set<DayOfWeek> days = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comic_genres",
            joinColumns = @JoinColumn(name = "comic_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Comic(String title, String author, String description, String thumbnail,
                 Integer freeEpisodeCount, ComicStatus status) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnail = thumbnail;
        this.freeEpisodeCount = freeEpisodeCount != null ? freeEpisodeCount : 3;
        this.status = status != null ? status : ComicStatus.ONGOING;
    }

    public void update(String title, String author, String description, String thumbnail,
                       Integer freeEpisodeCount, ComicStatus status) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnail = thumbnail;
        this.freeEpisodeCount = freeEpisodeCount;
        this.status = status;
    }

    public void updateViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public void updateAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public void updateDays(Set<DayOfWeek> days) {
        this.days.clear();
        this.days.addAll(days);
    }

    public void updateGenres(Set<Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
    }
}
