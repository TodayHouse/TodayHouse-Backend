package com.todayhouse.domain.category.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.category.domain.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)//@BeforeAll 사용
class CategoryRepositoryTest extends DataJpaBase {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TestEntityManager em;

    @BeforeAll
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

        List<Category> categories = em.getEntityManager().createQuery("select c from Category c", Category.class).getResultList();
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
    void category_Id로_삭제() {
        Category par = Category.builder().name("par").build();
        em.persist(par);
        em.flush();
        em.clear();

        categoryRepository.deleteById(par.getId());

        List<Category> categories = em.getEntityManager().createQuery("select c from Category c", Category.class).getResultList();
        assertEquals(0, categories.size());
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
}