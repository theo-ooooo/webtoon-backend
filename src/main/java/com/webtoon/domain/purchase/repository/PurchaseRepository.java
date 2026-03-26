package com.webtoon.domain.purchase.repository;

import com.webtoon.domain.purchase.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, PurchaseRepositoryCustom {
    Optional<Purchase> findByUserIdAndEpisodeId(Long userId, Long episodeId);
    boolean existsByUserIdAndEpisodeId(Long userId, Long episodeId);
    Page<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId, Pageable pageable);
}
