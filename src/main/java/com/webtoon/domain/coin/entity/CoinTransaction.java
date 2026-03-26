package com.webtoon.domain.coin.entity;

import com.webtoon.domain.purchase.entity.Purchase;
import com.webtoon.domain.user.entity.User;
import com.webtoon.global.enums.CoinTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coin_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinTransactionType type;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_purchase_id")
    private Purchase relatedPurchase;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public CoinTransaction(User user, CoinTransactionType type, Integer amount,
                           String reason, Purchase relatedPurchase) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.reason = reason;
        this.relatedPurchase = relatedPurchase;
    }
}
