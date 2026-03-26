package com.webtoon.domain.readhistory.repository;

import com.webtoon.domain.readhistory.entity.ReadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadHistoryRepository extends JpaRepository<ReadHistory, Long> {
    Optional<ReadHistory> findByUserIdAndComicId(Long userId, Long comicId);
    List<ReadHistory> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
