package com.webtoon.domain.coin.dto.response;

import com.webtoon.domain.coin.entity.CoinTransaction;
import com.webtoon.global.enums.CoinTransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CoinTransactionResponse {
    private Long id;
    private CoinTransactionType type;
    private Integer amount;
    private String reason;
    private LocalDateTime createdAt;

    public static CoinTransactionResponse from(CoinTransaction tx) {
        return new CoinTransactionResponse(
                tx.getId(), tx.getType(), tx.getAmount(),
                tx.getReason(), tx.getCreatedAt()
        );
    }
}
