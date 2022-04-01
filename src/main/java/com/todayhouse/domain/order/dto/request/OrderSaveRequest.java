package com.todayhouse.domain.order.dto.request;

import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.domain.User;
import lombok.Getter;

@Getter
public class OrderSaveRequest {
    String memo;
    Long userId;
    Long productId;
    Long parentOptionId;
    Long childOptionId;
    Long selectionOptionId;
    Integer productQuantity;
    Integer selectionQuantity;


    public Order toEntity(User user, Product product, ParentOption parentOption, ChildOption childOption,
                          SelectionOption selectionOption){
        return Order.builder()
                .memo(memo)
                .user(user)
                .product(product)
                .parentOption(parentOption)
                .childOption(childOption)
                .selectionOption(selectionOption)
                .productQuantity(productQuantity)
                .selectionQuantity(selectionQuantity)
                .build();
    }
}
