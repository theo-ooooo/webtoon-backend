package com.webtoon.domain.episode.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "episode_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "image_order", nullable = false)
    private Integer order;

    @Builder
    public EpisodeImage(Episode episode, String imageUrl, Integer order) {
        this.episode = episode;
        this.imageUrl = imageUrl;
        this.order = order;
    }
}
