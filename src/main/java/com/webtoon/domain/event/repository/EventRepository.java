package com.webtoon.domain.event.repository;

import com.webtoon.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate <= :now AND e.endDate >= :now ORDER BY e.createdAt DESC")
    List<Event> findActiveEvents(@Param("now") LocalDate now);
}
