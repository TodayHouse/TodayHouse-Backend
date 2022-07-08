package com.todayhouse.domain.story.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.todayhouse.domain.story.domain.QStory.story;

public class CustomStoryRepositoryImpl implements CustomStoryRepository {
    private final JPAQueryFactory queryFactory;

    public CustomStoryRepositoryImpl(EntityManager entityManager) {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Story> searchCondition(StorySearchRequest request, Pageable pageable) {
        List<Long> ids = queryFactory.select(story.id).from(story).where(
                        eqFamilyType(request.getFamilyType()),
                        eqResiType(request.getResiType()),
                        betweenFloorSpace(request.getFloorSpaceMin(), request.getFloorSpaceMax()),
                        eqStyleType(request.getStyleType()),
                        eqCategory(request.getCategory()),
                        containSearch(request.getSearch())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<Story> content = queryFactory.selectFrom(story)
                .leftJoin(story.user).fetchJoin()
                .where(story.id.in(ids))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int size = queryFactory.selectFrom(story).where(
                eqFamilyType(request.getFamilyType()),
                eqResiType(request.getResiType()),
                betweenFloorSpace(request.getFloorSpaceMin(), request.getFloorSpaceMax()),
                eqStyleType(request.getStyleType())).fetch().size();
        return new PageImpl<>(content, pageable, size);

    }

    private BooleanExpression eqCategory(Story.Category category) {
        if (category == null)
            return null;
        return story.category.eq(category);
    }

    private BooleanExpression eqStyleType(StyleType styleType) {
        if (styleType == null)
            return null;
        return story.styleType.eq(styleType);
    }

    private BooleanExpression eqResiType(ResiType resiType) {
        if (resiType == null)
            return null;
        return story.resiType.eq(resiType);
    }

    private BooleanExpression eqFamilyType(FamilyType familyType) {
        if (familyType == null)
            return null;
        return story.familyType.eq(familyType);
    }

    private BooleanExpression betweenFloorSpace(Integer floorSpaceMin, Integer floorSpaceMax) {
        if (floorSpaceMin == null || floorSpaceMax == null)
            return null;
        return story.floorSpace.between(floorSpaceMin, floorSpaceMax);
    }

    private BooleanExpression containSearch(String search) {
        if (ObjectUtils.isEmpty(search))
            return null;
        return story.user.nickname.contains(search).or(
                story.content.contains(search).or(
                        story.title.contains(search)
                )
        );
    }
}
