package com.todayhouse.domain.order.dao;

import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.QOrders;
import com.todayhouse.domain.product.domain.QChildOption;
import com.todayhouse.domain.product.domain.QParentOption;
import com.todayhouse.domain.product.domain.QProduct;
import com.todayhouse.domain.product.domain.QSelectionOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class OrderRepositoryImpl extends QuerydslRepositorySupport
        implements CustomOrderRepository {
    public OrderRepositoryImpl() {
        super(Orders.class);
    }

    @Override
    public Page<Orders> findByUserIdWithProductAndOptions(Long userId, Pageable pageable) {
        QOrders qOrder = QOrders.orders;
        QProduct qProduct = QProduct.product;
        QChildOption qChildOption = QChildOption.childOption;
        QParentOption qParentOption = QParentOption.parentOption;
        QSelectionOption qSelectionOption = QSelectionOption.selectionOption;

        JPQLQuery<Orders> query = from(qOrder)
                .innerJoin(qOrder.product, qProduct).fetchJoin()
                .innerJoin(qOrder.parentOption, qParentOption).fetchJoin()
                .leftJoin(qOrder.childOption, qChildOption).fetchJoin()
                .leftJoin(qOrder.selectionOption, qSelectionOption).fetchJoin()
                .where(qOrder.user.id.eq(userId));

        List<Orders> orders = getQuerydsl().applyPagination(pageable, query).fetch();
        long total = orders.size();
        return new PageImpl<>(orders, pageable, total);
    }
}
