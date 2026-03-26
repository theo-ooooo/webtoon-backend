package com.webtoon.domain.coin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CoinChargeRequest {

    @NotNull(message = "충전 수량은 필수입니다.")
    @Min(value = 1, message = "최소 1개 이상 충전해야 합니다.")
    private Integer amount;
}
