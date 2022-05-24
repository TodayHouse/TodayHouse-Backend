package com.todayhouse.domain.order.dao;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.todayhouse.domain.order.domain.QOrders.orders;

public class OrderRepositoryImpl extends QuerydslRepositorySupport
        implements CustomOrderRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public OrderRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Orders.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Orders> findAllByUserIdWithProductAndOptions(Long userId, Pageable pageable) {
        List<Orders> ordersList = getPagingOrders(userId, pageable);

        JPQLQuery<Orders> countQuery = from(orders)
                .innerJoin(orders.product).fetchJoin()
                .innerJoin(orders.parentOption).fetchJoin()
                .where(orders.user.id.eq(userId));

        return PageableExecutionUtils.getPage(ordersList, pageable, () -> countQuery.fetchCount());
    }

    private List<Orders> getPagingOrders(Long userId, Pageable pageable) {
        JPAQuery<Long> idQuery = jpaQueryFactory.select(orders.id)
                .from(orders)
                .innerJoin(orders.product)
                .innerJoin(orders.parentOption)
                .where(orders.user.id.eq(userId));
        List<Long> ids = getQuerydsl().applyPagination(pageable, idQuery).fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        JPQLQuery<Orders> query = from(orders)
                .from(orders)
                .innerJoin(orders.product).fetchJoin()
                .innerJoin(orders.parentOption).fetchJoin()
                .leftJoin(orders.childOption).fetchJoin()
                .leftJoin(orders.selectionOption).fetchJoin()
                .where(orders.id.in(ids));

        return getQuerydsl().applySorting(pageable.getSort(), query).fetch();
    }
}
