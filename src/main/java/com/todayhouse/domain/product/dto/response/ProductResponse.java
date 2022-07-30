package com.todayhouse.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.product.domain.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {
    private Long id;
    private Long sellerId;
    private String brand;
    private String title;
    private String option1;
    private String option2;
    private String productDetail;
    private String selectionOption;
    private int sales;
    private int price;
    private int deliveryFee;
    private int discountRate;
    private boolean specialPrice;

    private int likesCount;

    private boolean liked;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> imageUrls = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> categoryPath = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<ParentOptionResponse> parentOptions = new LinkedHashSet<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<SelectionOptionResponse> selectionOptions = new LinkedHashSet<>();

    // null safe로 parent, child, selection option 모두 response type으로 변경
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.sellerId = product.getSeller().getId();
        this.brand = product.getBrand();
        this.title = product.getTitle();
        this.option1 = product.getParentOption();
        this.option2 = product.getChildOption();
        this.selectionOption = product.getSelectionOption();
        this.price = product.getPrice();
        this.sales = product.getSales();
        this.deliveryFee = product.getDeliveryFee();
        this.discountRate = product.getDiscountRate();
        this.specialPrice = product.isSpecialPrice();
        this.productDetail = product.getProductDetail();
        this.parentOptions = Optional.ofNullable(product.getParents())
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(parentOption -> new ParentOptionResponse(parentOption, true)).collect(Collectors.toSet());
        this.selectionOptions = Optional.ofNullable(product.getSelectionOptions())
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(selectionOption -> new SelectionOptionResponse(selectionOption)).collect(Collectors.toSet());
        this.likesCount = product.getLikesCount();

    }

    public void setImages(List<String> images) {
        this.imageUrls = images;
    }

    public void addCategoryPath(String category) {
        this.categoryPath.add(category);
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
