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
                        familTypeEq(request.getFamilyType()),
                        ResiTypeEq(request.getResiType()),
                        floorSpaceBetween(request.getFloorSpaceMin(), request.getFloorSpaceMax()),
                        styleTypeEq(request.getStyleType()),
                        categoryEq(request.getCategory())
                ).
                offset(pageable.getOffset()).
                limit(pageable.getPageSize()).
                fetch();
        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<Story> content = queryFactory.selectFrom(story).
                where(story.id.in(ids)).
                offset(pageable.getOffset()).
                limit(pageable.getPageSize()).
                fetch();

        int size = queryFactory.selectFrom(story).where(
                familTypeEq(request.getFamilyType()),
                ResiTypeEq(request.getResiType()),
                floorSpaceBetween(request.getFloorSpaceMin(), request.getFloorSpaceMax()),
                styleTypeEq(request.getStyleType())).fetch().size();
        return new PageImpl<>(content, pageable, size);

    }

    private BooleanExpression categoryEq(Story.Category category) {
        if (category == null) {
            return null;
        } else {
            return story.category.eq(category);
        }
    }

    private BooleanExpression styleTypeEq(StyleType styleType) {
        if (styleType == null) {
            return null;
        } else {
            return story.styleType.eq(styleType);
        }
    }

    private BooleanExpression ResiTypeEq(ResiType resiType) {
        if (resiType == null) {
            return null;
        } else {
            return story.resiType.eq(resiType);
        }
    }

    private BooleanExpression familTypeEq(FamilyType familyType) {
        if (familyType == null) {
            return null;
        } else {

            return story.familyType.eq(familyType);
        }

    }

    private BooleanExpression floorSpaceBetween(Integer floorSpaceMin, Integer floorSpaceMax) {
        if (floorSpaceMin == null || floorSpaceMax == null) {
            return null;
        } else {
            return story.floorSpace.between(floorSpaceMin, floorSpaceMax);
        }
    }
}
