package com.todayhouse.domain.product.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Subgraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        List<Product> products = getPagingProducts(productSearch, pageable);

        JPQLQuery<Product> countQuery = from(product)
                .join(product.seller).fetchJoin()
                .where(eqBrand(productSearch.getBrand()),
                        goePrice(productSearch.getPriceFrom()),
                        loePrice(productSearch.getPriceTo()),
                        onlyDeliveryFee(productSearch.getDeliveryFee()),
                        inCategoryName(productSearch.getCategoryName()),
                        onlySpecialPrice(productSearch.getSpecialPrice()),
                        containSearch(productSearch.getSearch())
                );
        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchCount);
    }

    private List<Product> getPagingProducts(ProductSearchRequest productSearch, Pageable pageable) {
        JPAQuery<Long> idQuery = jpaQueryFactory.select(product.id)
                .from(product)
                .join(product.seller)
                .where(eqBrand(productSearch.getBrand()),
                        goePrice(productSearch.getPriceFrom()),
                        loePrice(productSearch.getPriceTo()),
                        onlyDeliveryFee(productSearch.getDeliveryFee()),
                        inCategoryName(productSearch.getCategoryName()),
                        onlySpecialPrice(productSearch.getSpecialPrice()),
                        containSearch(productSearch.getSearch())
                );
        List<Long> ids = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, idQuery).fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        JPQLQuery<Product> query = from(product)
                .from(product)
                .join(product.seller).fetchJoin()
                .where(product.id.in(ids));

        return getQuerydsl().applySorting(pageable.getSort(), query).fetch();
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
        if (deliveryFee == null || !deliveryFee)
            return null;
        return product.deliveryFee.gt(0);
    }

    private BooleanExpression onlySpecialPrice(Boolean specialPrice) {
        if (specialPrice == null || !specialPrice)
            return null;
        return product.specialPrice.isTrue();
    }

    private BooleanExpression inCategoryName(String categoryName) {
        if (ObjectUtils.isEmpty(categoryName))
            return null;
        List<Long> ids = categoryRepository.findOneByNameWithAllChildren(categoryName).stream()
                .map(Category::getId).collect(Collectors.toList());
        return product.category.id.in(ids);
    }

    private BooleanExpression containSearch(String search) {
        if (ObjectUtils.isEmpty(search))
            return null;
        return product.category.name.contains(search).or(
                product.brand.contains(search).or(
                        product.title.contains(search)
                )
        );
    }
}
