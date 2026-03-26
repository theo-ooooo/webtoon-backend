package com.webtoon.domain.purchase.repository;

import java.util.List;

public interface PurchaseRepositoryCustom {
    List<Long> findPurchasedEpisodeIds(Long userId, List<Long> episodeIds);
}
