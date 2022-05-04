package com.todayhouse.domain.order.dto.request;

import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.domain.User;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderSaveRequest {
    private String memo;
    @NotNull(message = "productId를 입력해 주세요.")
    private Long productId;
    @NotNull(message = "parentOptionId를 입력해 주세요.")
    private Long parentOptionId;
    private Long childOptionId;
    private Long selectionOptionId;
    @NotNull(message = "productQuantity를 입력해 주세요.")
    private int productQuantity;
    private int selectionQuantity = 0;
    @Valid
    @NotNull(message = "deliveryRequest를 입력해 주세요.")
    private DeliverySaveRequest deliverySaveRequest;

    public Orders toEntity(User user, Product product, ParentOption parentOption, ChildOption childOption,
                           SelectionOption selectionOption) {
        return Orders.builder()
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
