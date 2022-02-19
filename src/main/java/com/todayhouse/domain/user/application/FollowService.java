package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.dto.SimpleUser;

import java.util.Set;

public interface FollowService {
    Follow saveFollow(Long fromId, Long toId);

    int countFollowers(Long userId);

    int countFollowings(Long userId);

    Set<SimpleUser> findFollowers(Long userId);

    Set<SimpleUser> findFollowings(Long userId);

    void deleteFollow(Long fromId, Long toId);
}
