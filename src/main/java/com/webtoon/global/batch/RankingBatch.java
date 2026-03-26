package com.webtoon.global.batch;

import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.comic.repository.ComicRepository;
import com.webtoon.domain.episode.repository.EpisodeViewRepository;
import com.webtoon.domain.ranking.entity.PopularRanking;
import com.webtoon.domain.ranking.repository.PopularRankingRepository;
import com.webtoon.domain.rating.repository.RatingRepository;
import com.webtoon.global.enums.RankingPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatch {

    private final EpisodeViewRepository episodeViewRepository;
    private final PopularRankingRepository popularRankingRepository;
    private final ComicRepository comicRepository;
    private final RatingRepository ratingRepository;

    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시
    @Transactional
    public void calculateRanking() {
        log.info("인기 랭킹 배치 시작");

        // 일간 랭킹
        calculatePeriodRanking(RankingPeriod.DAILY, LocalDateTime.now().minusDays(1));

        // 주간 랭킹
        calculatePeriodRanking(RankingPeriod.WEEKLY, LocalDateTime.now().minusWeeks(1));

        // 총 조회수 갱신
        updateTotalViewCount();

        // 평균 별점 갱신
        updateAverageRating();

        log.info("인기 랭킹 배치 완료");
    }

    private void calculatePeriodRanking(RankingPeriod period, LocalDateTime since) {
        popularRankingRepository.deleteByPeriod(period);

        List<Object[]> results = episodeViewRepository.countByComicSince(since);
        int rank = 1;
        for (Object[] row : results) {
            Long comicId = (Long) row[0];
            Long viewCount = (Long) row[1];
            Comic comic = comicRepository.findById(comicId).orElse(null);
            if (comic == null) continue;

            popularRankingRepository.save(PopularRanking.builder()
                    .comic(comic)
                    .rank(rank++)
                    .viewCount(viewCount)
                    .period(period)
                    .build());
        }
    }

    private void updateTotalViewCount() {
        List<Object[]> results = episodeViewRepository.countByComic();
        for (Object[] row : results) {
            Long comicId = (Long) row[0];
            Long viewCount = (Long) row[1];
            comicRepository.findById(comicId)
                    .ifPresent(comic -> comic.updateViewCount(viewCount));
        }
    }

    private void updateAverageRating() {
        List<Object[]> results = ratingRepository.averageScoreByComic();
        for (Object[] row : results) {
            Long comicId = (Long) row[0];
            Double avg = (Double) row[1];
            comicRepository.findById(comicId)
                    .ifPresent(comic -> comic.updateAverageRating(
                            BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP)));
        }
    }
}
