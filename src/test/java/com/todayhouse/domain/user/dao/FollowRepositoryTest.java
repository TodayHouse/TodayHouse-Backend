package com.todayhouse.domain.user.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
class FollowRepositoryTest extends DataJpaBase {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("팔로잉 수 구하기")
    void countByFollowerId() {
        // user1는 3명을 팔로잉
        insertFollow(em);
        User findUser = em.getEntityManager().createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", "user1").getSingleResult();

        long count = followRepository.countByFromId(findUser.getId());

        assertEquals(3, count);
    }

    @Test
    @DisplayName("팔로워 수 구하기")
    void countByFollowingId() {
        // user4는 2명의 팔로워 존재
        insertFollow(em);
        User findUser = em.getEntityManager().createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", "user4").getSingleResult();

        long count = followRepository.countByToId(findUser.getId());

        assertEquals(2, count);
    }

    @Test
    @DisplayName("팔로우 추가")
    void addFollowing() {
        //given
        User user1 = User.builder().nickname("user1").profileImage("1").introduction("1111").build();
        User user2 = User.builder().nickname("user2").profileImage("2").introduction("2222").build();

        em.persist(user1);
        em.persist(user2);

        //when
        Follow follow = Follow.builder().from(user1).to(user2).build();
        em.persist(follow);
        em.flush();

        //then
        Follow find = em.getEntityManager()
                .createQuery("select f from Follow f where f.from.id = :from and f.to.id = :to", Follow.class)
                .setParameter("from", user1.getId())
                .setParameter("to", user2.getId()).getSingleResult();
        assertThat(find).isEqualTo(follow);
    }

    @Test
    @DisplayName("팔로우 삭제")
    void removeFollowing() {
        //given
        User user1 = User.builder().nickname("user1").profileImage("1").introduction("1111").build();
        User user2 = User.builder().nickname("user2").profileImage("2").introduction("2222").build();
        em.persist(user1);
        em.persist(user2);
        em.persist(Follow.builder().from(user1).to(user2).build());

        //when
        followRepository.deleteByFromIdAndToId(user1.getId(), user2.getId());

        //then
        List<Follow> resultList = em.getEntityManager()
                .createQuery("select f from Follow f where f.from.id = :from and f.to.id = :to", Follow.class)
                .setParameter("from", user1.getId())
                .setParameter("to", user2.getId()).getResultList();
        assertThat(resultList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("팔로우 여부")
    void exist() {
        User user1 = User.builder().nickname("user1").profileImage("1").introduction("1111").build();
        User user2 = User.builder().nickname("user2").profileImage("2").introduction("2222").build();
        em.persist(user1);
        em.persist(user2);
        em.persist(Follow.builder().from(user1).to(user2).build());

        assertThat(followRepository.existsFollowByFromIdAndToId(user1.getId(), user2.getId())).isTrue();
        assertThat(followRepository.existsFollowByFromIdAndToId(user2.getId(), user1.getId())).isFalse();
    }

    public static void insertFollow(TestEntityManager em) {
        User user1 = User.builder().nickname("user1").profileImage("1").introduction("1111").build();
        User user2 = User.builder().nickname("user2").profileImage("2").introduction("2222").build();
        User user3 = User.builder().nickname("user3").profileImage("3").introduction("3333").build();
        User user4 = User.builder().nickname("user4").profileImage("4").introduction("4444").build();

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
}