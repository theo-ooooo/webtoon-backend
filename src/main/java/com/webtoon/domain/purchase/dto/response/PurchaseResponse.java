package com.webtoon.domain.purchase.dto.response;

import com.webtoon.domain.purchase.entity.Purchase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PurchaseResponse {
    private Long id;
    private Long episodeId;
    private String comicTitle;
    private String episodeTitle;
    private Integer coinUsed;
    private LocalDateTime purchasedAt;

    public static PurchaseResponse from(Purchase purchase) {
        return new PurchaseResponse(
                purchase.getId(),
                purchase.getEpisode().getId(),
                purchase.getEpisode().getComic().getTitle(),
                purchase.getEpisode().getTitle(),
                purchase.getCoinUsed(),
                purchase.getPurchasedAt()
        );
    }
}
