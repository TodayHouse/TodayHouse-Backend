package com.todayhouse.domain.order.dao;

import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.todayhouse.domain.order.domain.QOrders.orders;

public class OrderRepositoryImpl extends QuerydslRepositorySupport
        implements CustomOrderRepository {
    public OrderRepositoryImpl() {
        super(Orders.class);
    }

    @Override
    public Page<Orders> findAllByUserIdWithProductAndOptions(Long userId, Pageable pageable) {
        JPQLQuery<Orders> query = from(orders)
                .innerJoin(orders.product).fetchJoin()
                .innerJoin(orders.parentOption).fetchJoin()
                .leftJoin(orders.childOption).fetchJoin()
                .leftJoin(orders.selectionOption).fetchJoin()
                .where(orders.user.id.eq(userId));
        List<Orders> ordersList = getQuerydsl().applyPagination(pageable, query).fetch();

        JPQLQuery<Orders> countQuery = from(orders)
                .innerJoin(orders.product).fetchJoin()
                .innerJoin(orders.parentOption).fetchJoin()
                .where(orders.user.id.eq(userId));

        return PageableExecutionUtils.getPage(ordersList, pageable, () -> countQuery.fetchCount());
    }
}
