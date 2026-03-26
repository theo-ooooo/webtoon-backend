package com.webtoon.domain.ranking.repository;

import com.webtoon.domain.ranking.entity.PopularRanking;
import com.webtoon.global.enums.RankingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopularRankingRepository extends JpaRepository<PopularRanking, Long> {

    @Query("SELECT pr FROM PopularRanking pr JOIN FETCH pr.comic WHERE pr.period = :period ORDER BY pr.rank ASC")
    List<PopularRanking> findByPeriodOrderByRankAsc(@Param("period") RankingPeriod period);

    @Modifying
    @Query("DELETE FROM PopularRanking pr WHERE pr.period = :period")
    void deleteByPeriod(@Param("period") RankingPeriod period);
}
