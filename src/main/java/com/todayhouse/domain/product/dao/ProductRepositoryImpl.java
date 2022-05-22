package com.todayhouse.domain.product.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Subgraph;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.todayhouse.domain.product.domain.QProduct.product;

public class ProductRepositoryImpl extends QuerydslRepositorySupport
        implements CustomProductRepository {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory jpaQueryFactory;
    private final CategoryRepository categoryRepository;

    public ProductRepositoryImpl(JPAQueryFactory jpaQueryFactory, CategoryRepository categoryRepository) {
        super(Product.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Product> findAllWithSeller(ProductSearchRequest productSearch, Pageable pageable) {
        JPQLQuery<Product> query = from(product)
                .join(product.seller)
                .where(eqBrand(productSearch.getBrand()),
                        goePrice(productSearch.getPriceFrom()),
                        loePrice(productSearch.getPriceTo()),
                        onlyDeliveryFee(productSearch.getDeliveryFee()),
                        inCategoryName(productSearch.getCategoryName()),
                        onlySpecialPrice(productSearch.getSpecialPrice()));
        List<Product> products = getQuerydsl().applyPagination(pageable, query).fetch();

        JPQLQuery<Product> countQuery = from(product)
                .join(product.seller)
                .where(eqBrand(productSearch.getBrand()),
                        goePrice(productSearch.getPriceFrom()),
                        loePrice(productSearch.getPriceTo()),
                        onlyDeliveryFee(productSearch.getDeliveryFee()),
                        inCategoryName(productSearch.getCategoryName()),
                        onlySpecialPrice(productSearch.getSpecialPrice()));

        return PageableExecutionUtils.getPage(products, pageable, () -> countQuery.fetchCount());
    }

    // product를 seller, 모든 option과 left join
    @Override
    public Optional<Product> findByIdWithOptionsAndSeller(Long id) {
        //seller, selectionOptions, parentOption-childOption 과 fetch join
        EntityGraph<Product> graph = em.createEntityGraph(Product.class);
        graph.addAttributeNodes("seller", "selectionOptions");
        Subgraph<ParentOption> options = graph.addSubgraph("parents");
        options.addAttributeNodes("children");

        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product).where(product.id.eq(id));
        query.setHint("javax.persistence.fetchgraph", graph);
        return Optional.ofNullable(query.fetchOne());
    }

    private BooleanExpression eqBrand(String brand) {
        if (ObjectUtils.isEmpty(brand))
            return null;
        return product.brand.eq(brand);
    }

    private BooleanExpression goePrice(Integer price) {
        if (price == null)
            return null;
        return product.price.goe(price);
    }

    private BooleanExpression loePrice(Integer price) {
        if (price == null)
            return null;
        return product.price.loe(price);
    }

    private BooleanExpression onlyDeliveryFee(Boolean deliveryFee) {
        if (deliveryFee == null || deliveryFee.booleanValue() == false)
            return null;
        return product.deliveryFee.gt(0);
    }

    private BooleanExpression onlySpecialPrice(Boolean specialPrice) {
        if (specialPrice == null || specialPrice.booleanValue() == false)
            return null;
        return product.specialPrice.isTrue();
    }

    private BooleanExpression inCategoryName(String categoryName) {
        if (ObjectUtils.isEmpty(categoryName))
            return null;
        List<Long> ids = categoryRepository.findOneByNameWithAllChildren(categoryName).stream()
                .map(category -> category.getId()).collect(Collectors.toList());
        return product.category.id.in(ids);
    }
}
