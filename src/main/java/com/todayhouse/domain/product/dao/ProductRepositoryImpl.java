package com.todayhouse.domain.product.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.domain.QCategory;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.QProduct;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.user.domain.QSeller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Subgraph;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl extends QuerydslRepositorySupport
        implements CustomProductRepository {

    @PersistenceContext
    EntityManager em;

//    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryImpl() {
        super(Product.class);
        //this.jpaQueryFactory = jpaQueryFactory;
    }

    //product 페이징
    @Override
    public Page<Product> findAll(ProductSearchRequest productSearch, Pageable pageable) {
        QProduct qProduct = QProduct.product;
        QSeller qSeller = QSeller.seller;

        JPQLQuery<Product> query = from(qProduct).join(qProduct.seller, qSeller);

        makeProductSearchQuery(query, productSearch);

        query.fetchJoin();
        QueryResults<Product> results = getQuerydsl().applyPagination(pageable, query).fetchResults();

        List<Product> products = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Optional<Product> findByIdWithOptionsAndSeller(Long id) {
        QProduct qProduct = QProduct.product;

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

        //seller, selectionOptions, parentOption-childOption 과 fetch join
        EntityGraph<Product> graph = em.createEntityGraph(Product.class);
        graph.addAttributeNodes("seller", "selectionOptions");
        Subgraph<ParentOption> options = graph.addSubgraph("options");
        options.addAttributeNodes("children");

        JPAQuery<Product> query = jpaQueryFactory.selectFrom(qProduct).where(qProduct.id.eq(id));
        query.setHint("javax.persistence.fetchgraph", graph);
        return Optional.ofNullable(query.fetchOne());
    }

    // ProductSearchRequest의 조건 where절에 추가
    private void makeProductSearchQuery(JPQLQuery<Product> query, ProductSearchRequest productSearch) {
        if (productSearch == null) return;

        QProduct qProduct = QProduct.product;

        if (productSearch.getBrand() != null)
            query.where(qProduct.brand.eq(productSearch.getBrand()));
        if (productSearch.getPriceFrom() != null)
            query.where(qProduct.price.goe(productSearch.getPriceFrom()));
        if (productSearch.getPriceTo() != null)
            query.where(qProduct.price.loe(productSearch.getPriceTo()));
        if (productSearch.isDeliveryFee())
            query.where(qProduct.deliveryFee.gt(0));
        if (productSearch.isSpecialPrice())
            query.where(qProduct.specialPrice.isTrue());

        List<Long> ids = getCategoryIds(productSearch.getCategoryId());

        if (ids != null)
            query.where(qProduct.category.id.in(ids));
    }

    // 해당 카테고리 id와 모든 하위 카테고리 id를 list에 추가
    private List<Long> getCategoryIds(Long categoryId) {
        if (categoryId == null) return null;

        QCategory qCategory = QCategory.category;

        Category category = from(qCategory).where(qCategory.id.eq(categoryId)).fetchOne();
        if (category == null) throw new CategoryNotFoundException();

        List<Long> ids = new LinkedList<>();
        getIds(category, ids);
        return ids;
    }

    private void getIds(Category category, List<Long> ids) {
        ids.add(category.getId());
        for (Category c : category.getChildren()) {
            getIds(c, ids);
        }
    }

}
