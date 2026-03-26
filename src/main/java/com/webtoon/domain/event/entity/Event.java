package com.webtoon.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String linkUrl;

    private String bgColor;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Event(String title, String description, String imageUrl, String linkUrl,
                 String bgColor, Boolean isActive, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.bgColor = bgColor;
        this.isActive = isActive != null ? isActive : true;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
