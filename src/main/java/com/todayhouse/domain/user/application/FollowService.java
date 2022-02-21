package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.dto.SimpleUser;

import java.util.Set;

public interface FollowService {
    Follow saveFollow(Long fromId, Long toId);

    long countFollowers(Long userId);

    long countFollowings(Long userId);

    Set<SimpleUser> findFollowers(Long userId);

    Set<SimpleUser> findFollowings(Long userId);

    void deleteFollow(Long fromId, Long toId);

    boolean isFollowing(Long fromId, Long toId);
}
