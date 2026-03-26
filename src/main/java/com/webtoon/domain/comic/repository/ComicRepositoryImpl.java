package com.webtoon.domain.comic.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webtoon.domain.comic.entity.Comic;
import com.webtoon.domain.comic.entity.QComic;
import com.webtoon.domain.genre.entity.QGenre;
import com.webtoon.global.enums.ComicStatus;
import com.webtoon.global.enums.DayOfWeek;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class ComicRepositoryImpl implements ComicRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QComic comic = QComic.comic;

    @Override
    public Page<Comic> findByDay(DayOfWeek day, Pageable pageable) {
        List<Comic> content = queryFactory
                .selectDistinct(comic)
                .from(comic)
                .where(comic.days.any().eq(day), comic.status.eq(ComicStatus.ONGOING))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comic.countDistinct())
                .from(comic)
                .where(comic.days.any().eq(day), comic.status.eq(ComicStatus.ONGOING));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Comic> findByStatus(ComicStatus status, Pageable pageable) {
        List<Comic> content = queryFactory
                .selectFrom(comic)
                .where(comic.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comic.count())
                .from(comic)
                .where(comic.status.eq(status));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Comic> findByGenreId(Long genreId, Pageable pageable) {
        QGenre genre = QGenre.genre;

        List<Comic> content = queryFactory
                .selectDistinct(comic)
                .from(comic)
                .join(comic.genres, genre)
                .where(genre.id.eq(genreId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comic.countDistinct())
                .from(comic)
                .join(comic.genres, genre)
                .where(genre.id.eq(genreId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Comic> search(String keyword, Pageable pageable) {
        BooleanExpression condition = comic.title.containsIgnoreCase(keyword)
                .or(comic.author.containsIgnoreCase(keyword));

        List<Comic> content = queryFactory
                .selectFrom(comic)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comic.count())
                .from(comic)
                .where(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
