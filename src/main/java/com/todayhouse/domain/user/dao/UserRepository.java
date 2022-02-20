package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.SimpleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndNicknameIsNotNull(String email);

    boolean existsByNickname(String nickname);

    // 팔로잉 찾기
    @Query("select new com.todayhouse.domain.user.dto.SimpleUser(u.id, u.nickname, u.introduction, u.profileImage) " +
            "from User u join Follow f on u.id = f.to.id " +
            "where f.from.id = :id")
    Set<SimpleUser> findFollowingsByFromId(@Param("id") Long id);

    // 팔로워 찾기
    @Query("select new com.todayhouse.domain.user.dto.SimpleUser(u.id, u.nickname, u.introduction, u.profileImage) " +
            "from User u join Follow f on u.id = f.from.id " +
            "where f.to.id = :id")
    Set<SimpleUser> findFollowersByToId(@Param("id") Long id);
}
