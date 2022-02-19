package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.dao.FollowRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.SimpleUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @InjectMocks
    FollowServiceImpl followService;

    @Mock
    UserRepository userRepository;

    @Mock
    FollowRepository followRepository;

    @Test
    @DisplayName("팔로우 관계 저장")
    void saveFollow() {
        Long fromId = 1L;
        Long toId = 2L;
        User from = User.builder().id(fromId).email("from@email").build();
        User to = User.builder().id(fromId).email("to@email").build();
        Follow follow = Follow.builder().from(from).to(to).build();

        when(userRepository.findById(fromId)).thenReturn(Optional.ofNullable(from));
        when(userRepository.findById(toId)).thenReturn(Optional.ofNullable(to));
        checkEmailInvalidation(from.getEmail());
        when(followRepository.save(any(Follow.class))).thenReturn(follow);

        assertEquals(followService.saveFollow(fromId, toId), follow);
    }

    @Test
    @DisplayName("팔로워 수 세기")
    void countFollowers() {
        long follower = 100;
        when(followRepository.countByToId(anyLong())).thenReturn(100L);

        assertEquals(followService.countFollowers(1L), follower);
    }

    @Test
    @DisplayName("팔로잉 수 세기")
    void countFollowings() {
        long follower = 100;
        when(followRepository.countByFromId(anyLong())).thenReturn(100L);

        assertEquals(followService.countFollowings(1L), follower);
    }

    @Test
    @DisplayName("팔로워 리스트 구하기")
    void findFollowers() {
        Set<SimpleUser> followers = new HashSet<>();
        when(userRepository.findFollowersByToId(anyLong())).thenReturn(followers);

        assertEquals(followService.findFollowers(1L), followers);
    }

    @Test
    @DisplayName("팔로잉 리스트 구하기")
    void findFollowings() {
        Set<SimpleUser> followers = new HashSet<>();
        when(userRepository.findFollowingsByFromId(anyLong())).thenReturn(followers);

        assertEquals(followService.findFollowings(1L), followers);
    }

    @Test
    @DisplayName("팔로우 끊기")
    void deleteFollow() {
        Long fromId = 1L;
        User user = User.builder().id(fromId).nickname("test").email("test@email.com").build();
        when(userRepository.findById(fromId)).thenReturn(Optional.ofNullable(user));
        checkEmailInvalidation(user.getEmail());
        doNothing().when(followRepository).deleteByFromIdAndToId(eq(fromId), anyLong());

        followService.deleteFollow(1L, 2L);

        verify(followRepository).deleteByFromIdAndToId(anyLong(), anyLong());
    }

    private void checkEmailInvalidation(String email) {
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}