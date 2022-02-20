package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countByFromId(Long id);

    long countByToId(Long id);

    void deleteByFromIdAndToId(Long fromId, Long toId);

    boolean existsFollowByFromIdAndToId(Long fromId, Long ToId);
}
