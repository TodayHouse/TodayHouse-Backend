package com.todayhouse.domain.user.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.user.domain.Agreement;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRepositoryTest extends DataJpaBase {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    User user = User.builder().email("test@test.com").nickname("test")
            .authProvider(AuthProvider.LOCAL).password("abc1234")
            .profileImage("img.jpg").roles(Collections.singletonList(Role.GUEST)).agreement(Agreement.agreeAll())
            .build();

    @Test
    @DisplayName("user 저장 후 email로 user 검색")
    void findByEmail() {
        userRepository.save(user);

        assertThat(userRepository.findByEmail(user.getEmail())).isEqualTo(Optional.ofNullable(user));
    }

    @Test
    @DisplayName("없는 email 검색")
    void findByInvalidEmail() {
        testEntityManager.persist(user);

        assertThat(userRepository.findByEmail("invalid")).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("email이 있는지 검색")
    void existsByEmail() {
        testEntityManager.persist(user);

        assertThat(userRepository.existsByEmailAndNicknameIsNotNull(user.getEmail())).isTrue();
    }

    @Test
    @DisplayName("nickname이 있는지 검색")
    void existsByNickname() {
        testEntityManager.persist(user);

        assertThat(userRepository.existsByNickname(user.getNickname())).isTrue();
    }
}