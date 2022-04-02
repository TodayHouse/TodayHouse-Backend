package com.todayhouse.domain.category.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.category.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryRepositoryTest extends DataJpaBase {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void category_저장() {
        Category par = Category.builder().name("par").build();
        Category ch = Category.builder().name("ch").parent(par).build();

        Category savePar = categoryRepository.save(par);

        Category findPar = em.find(Category.class, savePar.getId());
        assertEquals(findPar.getChildren().get(0), ch);
        assertEquals(findPar.getChildren().get(0).getDepth(), 1);
    }

    @Test
    void category_삭제() {
        Category par = Category.builder().name("par").build();
        Category ch = Category.builder().name("ch").parent(par).build();

        Category savePar = em.persist(par);

        categoryRepository.deleteById(savePar.getId());

        List<Category> categories = em.getEntityManager().createQuery("select c from Category c where c.id=:id", Category.class)
                .setParameter("id", par.getId()).getResultList();
        assertEquals(categories.size(), 0);
    }

    @Test
    void category_수정() {
        Category par = Category.builder().name("par").build();
        Category ch = Category.builder().name("ch").parent(par).build();

        Category savePar = em.persist(par);
        savePar.getChildren().get(0).updateName("newCh");
        em.flush();
        em.clear();

        Category findCh = em.find(Category.class, ch.getId());
        assertTrue(findCh.getName().equals("newCh"));
    }

    @Test
    void category_찾기() {
        Category par = Category.builder().name("par").build();
        Category ch = Category.builder().name("ch").parent(par).build();

        em.persist(par);
        em.flush();
        em.clear();

        List<Category> all = categoryRepository.findAll();
        assertTrue(all.stream().anyMatch(c -> c.getName().equals("par")));
        assertTrue(all.stream().anyMatch(c -> c.getName().equals("ch")));
    }

    @Test
    void category_name으로_찾기() {
        Category par = Category.builder().name("par").build();
        em.persist(par);
        em.flush();
        em.clear();

        Category find = categoryRepository.findByName("par").orElse(null);
        assertEquals("par", find.getName());
    }

    @Test
    void 같은_이름의_category_확인() {
        Category par = Category.builder().name("par").build();
        em.persist(par);
        em.flush();
        em.clear();

        boolean flag = categoryRepository.existsByName("par");

        assertTrue(flag);
    }

    @Test
    void depth로_찾기() {
        Category par = Category.builder().name("par").build();
        Category ch = Category.builder().name("ch").parent(par).build();

        em.persist(par);
        em.flush();
        em.clear();

        List<Category> categories = categoryRepository.findByDepth(1);
        assertEquals("ch", categories.get(0).getName());
    }

    @Test
    void subCategory_포함하여_찾기() {
        Category par = Category.builder().name("par").build();
        Category ch1 = Category.builder().name("ch1").parent(par).build();
        Category ch2 = Category.builder().name("ch2").parent(par).build();
        Category ch1ch1 = Category.builder().name("ch1ch1").parent(ch1).build();

        em.persist(par);
        em.flush();
        em.clear();

        List<Category> categories = categoryRepository.findOneWithAllChildrenById(par.getId());
        assertThat(categories.size()).isEqualTo(4);
        assertThat(categories.get(0).getId()).isEqualTo(par.getId());
        assertThat(categories.get(1).getId()).isEqualTo(ch1.getId());
        assertThat(categories.get(2).getId()).isEqualTo(ch2.getId());
        assertThat(categories.get(3).getId()).isEqualTo(ch1ch1.getId());
    }

    @Test
    void 모든_카테고리_깊이_부모_ID_오름차순_찾기() {
        List<Category> all = categoryRepository.findAll();
        for (Category c : all) {
            System.out.println(c.getName() + ", " + c.getId());
        }

        Category par1 = Category.builder().name("par1").build();
        Category ch1 = Category.builder().name("ch1").parent(par1).build();
        Category ch2 = Category.builder().name("ch2").parent(par1).build();
        Category ch1ch1 = Category.builder().name("ch1ch1").parent(ch1).build();

        Category par2 = Category.builder().name("par2").build();
        Category ch3 = Category.builder().name("ch3").parent(par2).build();
        Category ch3ch2 = Category.builder().name("ch3ch2").parent(ch3).build();

        em.persist(par1);
        em.persist(par2);
        em.flush();
        em.clear();

        List<Category> all2 = categoryRepository.findAll();
        for (Category c : all2) {
            System.out.println(c.getName() + ", " + c.getId());
        }
        List<Category> expect = List.of(par1, par2, ch1, ch2, ch3, ch1ch1, ch3ch2);
        List<Category> result = categoryRepository.findAllByOrderByDepthAscParentAscIdAsc();
        for (int i = 0; i < expect.size(); i++) {
            assertThat(result.get(i).getName()).isEqualTo(expect.get(i).getName());
        }
    }
}