package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.dao.FollowRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.SimpleUser;
import com.todayhouse.domain.user.exception.InvalidRequestException;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public Follow saveFollow(Long fromId, Long toId) {
        User from = userRepository.findById(fromId).orElseThrow(UserNotFoundException::new);
        checkEmailInvalidation(from.getEmail());
        User to = userRepository.findById(toId).orElseThrow(UserNotFoundException::new);

        Follow follow = Follow.builder().from(from).to(to).build();

        return followRepository.save(follow);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowers(Long userId) {
        return followRepository.countByToId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowings(Long userId) {
        return followRepository.countByFromId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SimpleUser> findFollowers(Long userId) {
        return userRepository.findFollowersByToId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SimpleUser> findFollowings(Long userId) {
        return userRepository.findFollowingsByFromId(userId);
    }

    @Override
    public void deleteFollow(Long fromId, Long toId) {
        User user = userRepository.findById(fromId).orElseThrow(UserNotFoundException::new);
        checkEmailInvalidation(user.getEmail());
        followRepository.deleteByFromIdAndToId(fromId, toId);
    }

    // jwt의 email과 맞는지 확인
    private void checkEmailInvalidation(String email) {
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.equals(jwtEmail))
            throw new InvalidRequestException();
    }
}
