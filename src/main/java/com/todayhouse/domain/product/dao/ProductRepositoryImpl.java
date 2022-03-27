package com.todayhouse.domain.product.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.category.dao.CategoryRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    //product 페이징, 필터링
    @Override
    public Page<Product> findAllWithSeller(ProductSearchRequest productSearch, Pageable pageable) {
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

    // product를 seller, 모든 option과 left join
    @Override
    public Optional<Product> findByIdWithOptionsAndSeller(Long id) {
        QProduct qProduct = QProduct.product;

        //seller, selectionOptions, parentOption-childOption 과 fetch join
        EntityGraph<Product> graph = em.createEntityGraph(Product.class);
        graph.addAttributeNodes("seller", "selectionOptions");
        Subgraph<ParentOption> options = graph.addSubgraph("parents");
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
        if (productSearch.getDeliveryFee() != null && productSearch.getDeliveryFee().booleanValue())
            query.where(qProduct.deliveryFee.gt(0));
        if (productSearch.getSpecialPrice() != null && productSearch.getSpecialPrice().booleanValue())
            query.where(qProduct.specialPrice.isTrue());

        if (productSearch.getCategoryId() != null) {
            List<Long> ids = categoryRepository.findOneWithAllChildrenById(productSearch.getCategoryId()).stream()
                    .map(category -> category.getId()).collect(Collectors.toList());
            if (!ids.isEmpty())
                query.where(qProduct.category.id.in(ids));
        }
    }
}
