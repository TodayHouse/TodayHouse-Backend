package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    int countByFromId(Long id);
    int countByToId(Long id);

    @Query("select new com.todayhouse.domain.user.dto.SimpleUser(u.nickname, u.introduction, u.profileImage) " +
            "from User u join Follow f on u.id = f.from.id " +
            "where u.id = :id")
    List<User> findUsersByFromId(Long id);

    @Query("select new com.todayhouse.domain.user.dto.SimpleUser(u.nickname, u.introduction, u.profileImage) " +
            "from User u join Follow f on u.id = f.to.id " +
            "where u.id = :id")
    List<User> findUsersByToId(Long id);
}
