package com.todayhouse.domain.order.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.todayhouse.domain.order.domain.QOrders.orders;

public class OrderRepositoryImpl extends QuerydslRepositorySupport
        implements CustomOrderRepository {
    public OrderRepositoryImpl() {
        super(Orders.class);
    }

    @Override
    public Page<Orders> findByUserIdWithProductAndOptions(Long userId, Pageable pageable) {
        JPQLQuery<Orders> query = from(orders)
                .innerJoin(orders.product).fetchJoin()
                .innerJoin(orders.parentOption).fetchJoin()
                .leftJoin(orders.childOption).fetchJoin()
                .leftJoin(orders.selectionOption).fetchJoin()
                .where(orders.user.id.eq(userId));

        QueryResults<Orders> results = getQuerydsl().applyPagination(pageable, query).fetchResults();
        List<Orders> ordersList = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(ordersList, pageable, total);
    }
}
