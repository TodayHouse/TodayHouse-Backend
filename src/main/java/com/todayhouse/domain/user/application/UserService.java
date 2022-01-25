package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSaveRequest;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    boolean existByEmail(String email);
    boolean existByNickname(String nickname);
    User saveUser(UserSaveRequest request);
    String login(UserLoginRequest request);
}
