package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class FollowRepositoryTest {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    TestEntityManager em;

    User user1, user2, user3, user4;

    @BeforeEach
    void preSet() {
        user1 = User.builder().nickname("user1").profileImage("1").introduction("1111").build();
        user2 = User.builder().nickname("user2").profileImage("2").introduction("2222").build();
        user3 = User.builder().nickname("user3").profileImage("3").introduction("3333").build();
        user4 = User.builder().nickname("user4").profileImage("4").introduction("4444").build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);

        em.persist(Follow.builder().from(user1).to(user2).build());
        em.persist(Follow.builder().from(user1).to(user3).build());
        em.persist(Follow.builder().from(user1).to(user4).build());
        em.persist(Follow.builder().from(user2).to(user4).build());
        em.persist(Follow.builder().from(user4).to(user1).build());
    }

    @Test
    @DisplayName("팔로잉 수 구하기")
    void countByFollowerId() {
        User findUser = em.getEntityManager().createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", "user1").getSingleResult();

        int count = followRepository.countByFromId(findUser.getId());

        assertEquals(3, count);
    }

    @Test
    @DisplayName("팔로워 수 구하기")
    void countByFollowingId() {
        User findUser = em.getEntityManager().createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", "user4").getSingleResult();

        int count = followRepository.countByToId(findUser.getId());

        assertEquals(2, count);
    }

    @Test
    void findUsersByFollowerId() {
    }

    @Test
    void findUsersByFollowingId() {
    }
}