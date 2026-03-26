package com.webtoon.domain.purchase.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webtoon.domain.purchase.entity.QPurchase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPurchase purchase = QPurchase.purchase;

    @Override
    public List<Long> findPurchasedEpisodeIds(Long userId, List<Long> episodeIds) {
        return queryFactory
                .select(purchase.episode.id)
                .from(purchase)
                .where(
                        purchase.user.id.eq(userId),
                        purchase.episode.id.in(episodeIds)
                )
                .fetch();
    }
}
