package com.todayhouse.domain.product.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionRepositoryTest extends DataJpaBase {
    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ParentOptionRepository parentOptionRepository;

    @Autowired
    ChildOptionRepository childOptionRepository;

    @Autowired
    SelectionOptionRepository selectionOptionRepository;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        Seller seller = Seller.builder().brand("testBrand").build();
        em.persist(seller);

        Product product = Product.builder().seller(seller).option1("o1").option2("o2").selectionOption("sp").build();

        ParentOption p1 = ParentOption.builder().content("p1").product(product).build();
        ChildOption p1c1 = ChildOption.builder().content("p1c1").parent(p1).build();
        ChildOption p1c2 = ChildOption.builder().content("p1c2").parent(p1).build();

        ParentOption p2 = ParentOption.builder().content("p2").product(product).build();
        ChildOption p2c1 = ChildOption.builder().content("p2c1").parent(p2).build();
        ChildOption p2c2 = ChildOption.builder().content("p2c2").parent(p2).build();

        SelectionOption s1 = SelectionOption.builder().content("s1").product(product).build();
        SelectionOption s2 = SelectionOption.builder().content("s2").product(product).build();
        SelectionOption s3 = SelectionOption.builder().content("s3").product(product).build();


        em.persist(product);

        em.flush();
        em.clear();
    }

    @Test
    void ParentOptionSet_구하기() {
        Product product = em.getEntityManager().createQuery("select p from Product p where p.brand = 'testBrand'", Product.class).getSingleResult();

        Set<ParentOption> parents = parentOptionRepository.findByProductId(product.getId());
        assertThat(parents.size()).isEqualTo(2);
        assertTrue(parents.stream().anyMatch(p -> p.getContent().equals("p1")
                && p.getChildren().size() == 2));
        assertTrue(parents.stream().anyMatch(p -> p.getContent().equals("p2")
                && p.getChildren().size() == 2));
    }

    @Test
    void ChildOptionSet_구하기() {
        ParentOption p1 = em.getEntityManager().createQuery("select p from ParentOption p where p.content = 'p1'", ParentOption.class).getSingleResult();

        Set<ChildOption> children = childOptionRepository.findByParentOptionId(p1.getId());

        assertThat(children.size()).isEqualTo(2);
        assertTrue(children.stream().anyMatch(p -> p.getContent().equals("p1c1")));
        assertTrue(children.stream().anyMatch(p -> p.getContent().equals("p1c2")));
    }

    @Test
    void SelectionOptionSet_구하기() {
        Product product = em.getEntityManager().createQuery("select p from Product p where p.brand = 'testBrand'", Product.class).getSingleResult();

        Set<SelectionOption> selections = selectionOptionRepository.findByProductId(product.getId());

        assertThat(selections.size()).isEqualTo(3);
        assertTrue(selections.stream().anyMatch(p -> p.getContent().equals("s1")));
        assertTrue(selections.stream().anyMatch(p -> p.getContent().equals("s2")));
        assertTrue(selections.stream().anyMatch(p -> p.getContent().equals("s3")));
    }
}
