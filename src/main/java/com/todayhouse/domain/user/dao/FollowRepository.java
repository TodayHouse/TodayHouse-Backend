package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    int countByFromId(Long id);

    int countByToId(Long id);

    void deleteByFromIdAndToId(Long fromId, Long toId);
}
