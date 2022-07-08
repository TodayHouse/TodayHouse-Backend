package com.todayhouse.domain.scrap.dao;

import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.todayhouse.domain.scrap.domain.QScrap.scrap;

public class ScrapRepositoryImpl extends QuerydslRepositorySupport
        implements CustomScrapRepository {
    public ScrapRepositoryImpl() {
        super(Scrap.class);
    }

    @Override
    public Page<Scrap> findScrapWithStoryByUser(Pageable pageable, User user) {
        List<Scrap> scraps = getPagingScraps(pageable, user);

        JPQLQuery<Scrap> countQuery = from(scrap)
                .innerJoin(scrap.story).fetchJoin()
                .where(scrap.user.eq(user).and(scrap.story.user.eq(user)));

        return PageableExecutionUtils.getPage(scraps, pageable, () -> countQuery.fetchCount());
    }

    private List<Scrap> getPagingScraps(Pageable pageable, User user) {
        JPQLQuery<Long> idQuery = from(scrap)
                .select(scrap.id)
                .innerJoin(scrap.story)
                .where(scrap.user.eq(user));
        List<Long> ids = getQuerydsl().applyPagination(pageable, idQuery).fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        JPQLQuery<Scrap> query = from(scrap)
                .innerJoin(scrap.story)
                .where(scrap.id.in(ids));

        return getQuerydsl().applySorting(pageable.getSort(), query).fetch();
    }
}
