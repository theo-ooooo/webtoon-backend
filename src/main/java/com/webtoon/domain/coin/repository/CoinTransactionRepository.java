package com.webtoon.domain.coin.repository;

import com.webtoon.domain.coin.entity.CoinTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    Page<CoinTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
