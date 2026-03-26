package com.webtoon.domain.episode.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webtoon.domain.episode.entity.QEpisode;
import com.webtoon.domain.episode.entity.QEpisodeView;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EpisodeViewRepositoryImpl implements EpisodeViewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QEpisodeView episodeView = QEpisodeView.episodeView;
    private final QEpisode episode = QEpisode.episode;

    @Override
    public List<Object[]> countByComicSince(LocalDateTime since) {
        List<Tuple> results = queryFactory
                .select(episode.comic.id, episodeView.count())
                .from(episodeView)
                .join(episodeView.episode, episode)
                .where(episodeView.viewedAt.goe(since))
                .groupBy(episode.comic.id)
                .orderBy(episodeView.count().desc())
                .fetch();

        return results.stream()
                .map(t -> new Object[]{t.get(episode.comic.id), t.get(episodeView.count())})
                .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> countByComic() {
        List<Tuple> results = queryFactory
                .select(episode.comic.id, episodeView.count())
                .from(episodeView)
                .join(episodeView.episode, episode)
                .groupBy(episode.comic.id)
                .fetch();

        return results.stream()
                .map(t -> new Object[]{t.get(episode.comic.id), t.get(episodeView.count())})
                .collect(Collectors.toList());
    }
}
