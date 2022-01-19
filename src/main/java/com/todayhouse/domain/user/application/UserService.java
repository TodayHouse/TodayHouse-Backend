package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSaveRequest;

import java.util.Optional;

public interface UserService {
    User save(UserSaveRequest request);
    Optional<User> findByEmail(String email);
    String login(UserLoginRequest request);
}
