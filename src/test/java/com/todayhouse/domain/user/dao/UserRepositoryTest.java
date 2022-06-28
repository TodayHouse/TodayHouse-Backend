package com.todayhouse.domain.user.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.user.domain.Agreement;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.SimpleUser;
import com.todayhouse.domain.user.dto.request.SellerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

class UserRepositoryTest extends DataJpaBase {

    @Autowired
    TestEntityManager em;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().email("test@test.com").nickname("test")
                .authProvider(AuthProvider.LOCAL).password("abc1234")
                .profileImage("img.jpg").roles(Collections.singletonList(Role.GUEST)).agreement(Agreement.agreeAll())
                .build();
        em.persist(user);
        em.flush();
        em.clear();
    }


    @Test
    @DisplayName("user 저장 후 email로 user 검색")
    void findByEmail() {
        User user = userRepository.findByEmail(this.user.getEmail()).orElse(null);
        assertThat(user.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("없는 email 검색")
    void findByInvalidEmail() {
        assertThat(userRepository.findByEmail("invalid")).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("email이 있는지 검색")
    void existsByEmail() {
        assertThat(userRepository.existsByEmailAndNicknameIsNotNull(user.getEmail())).isTrue();
    }

    @Test
    @DisplayName("nickname이 있는지 검색")
    void existsByNickname() {
        assertThat(userRepository.existsByNickname(user.getNickname())).isTrue();
    }

    @Test
    @DisplayName("팔로잉 유저 리스트 구하기")
    void findUsersByFollowerId() {
        //user1 은 user2,3,4를 팔로우
        //given
        FollowRepositoryTest.insertFollow(em);
        User findUser = em.getEntityManager().createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", "user1").getSingleResult();
        //when
        Set<SimpleUser> set = userRepository.findFollowingsByFromId(findUser.getId());
        //then
        assertThat(set, contains(
                hasProperty("nickname", is("user2")),
                hasProperty("nickname", is("user3")),
                hasProperty("nickname", is("user4"))
        ));
    }

    @Test
    @DisplayName("팔로워 유저 리스트 구하기")
    void findUsersByFollowingId() {
        //user1,2는 user4를 팔로우
        //given
        FollowRepositoryTest.insertFollow(em);
        User findUser = em.getEntityManager().createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", "user4").getSingleResult();
        //when
        Set<SimpleUser> set = userRepository.findFollowersByToId(findUser.getId());
        //then
        assertThat(set, contains(
                hasProperty("nickname", is("user1")),
                hasProperty("nickname", is("user2"))
        ));
    }

    @Test
    @DisplayName("user와 seller를 join해서 찾기")
    void findBySellerIdWithSeller() {
        SellerRequest request = SellerRequest.builder().brand("test").build();
        user.createSeller(request);
        User save = userRepository.save(user);
        Long sellerId = save.getSeller().getId();

        User user = userRepository.findBySellerIdWithSeller(sellerId).orElse(null);
        assertThat(user.getSeller().getBrand()).isEqualTo("test");
    }
}