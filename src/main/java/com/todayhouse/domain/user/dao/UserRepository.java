package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndNicknameIsNotNull(String email);

    boolean existsByNickname(String nickname);
}
