package com.todayhouse.domain.order.dto.request;

import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.domain.User;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class OrderSaveRequest {
    String memo;
    @NotNull(message = "productId를 입력해 주세요.")
    Long productId;
    @NotNull(message = "parentOptionId를 입력해 주세요.")
    Long parentOptionId;
    Long childOptionId;
    Long selectionOptionId;
    @NotNull(message = "productQuantity를 입력해 주세요.")
    int productQuantity = 0;
    int selectionQuantity = 0;

    public Order toEntity(User user, Product product, ParentOption parentOption, ChildOption childOption,
                          SelectionOption selectionOption) {
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
