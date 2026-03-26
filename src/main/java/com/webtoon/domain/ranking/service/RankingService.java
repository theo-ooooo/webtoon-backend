package com.webtoon.domain.ranking.service;

import com.webtoon.domain.ranking.dto.response.PopularRankingResponse;
import com.webtoon.domain.ranking.repository.PopularRankingRepository;
import com.webtoon.global.enums.RankingPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final PopularRankingRepository popularRankingRepository;

    public List<PopularRankingResponse> getRanking(RankingPeriod period) {
        return popularRankingRepository.findByPeriodOrderByRankAsc(period).stream()
                .map(PopularRankingResponse::from)
                .toList();
    }
}
