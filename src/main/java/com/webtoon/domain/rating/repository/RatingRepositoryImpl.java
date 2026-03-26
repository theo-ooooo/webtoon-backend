package com.webtoon.domain.rating.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webtoon.domain.episode.entity.QEpisode;
import com.webtoon.domain.rating.entity.QRating;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QRating rating = QRating.rating;
    private final QEpisode episode = QEpisode.episode;

    @Override
    public List<Object[]> averageScoreByComic() {
        List<Tuple> results = queryFactory
                .select(episode.comic.id, rating.score.avg())
                .from(rating)
                .join(rating.episode, episode)
                .groupBy(episode.comic.id)
                .fetch();

        return results.stream()
                .map(t -> new Object[]{t.get(episode.comic.id), t.get(rating.score.avg())})
                .collect(Collectors.toList());
    }

    @Override
    public Double averageScoreByComicId(Long comicId) {
        return queryFactory
                .select(rating.score.avg())
                .from(rating)
                .join(rating.episode, episode)
                .where(episode.comic.id.eq(comicId))
                .fetchOne();
    }
}
