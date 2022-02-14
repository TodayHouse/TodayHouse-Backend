package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    boolean existByEmail(String email);

    boolean existByNickname(String nickname);

    User saveUser(UserSignupRequest request);

    String login(UserLoginRequest request);

    void updatePassword(String email, PasswordUpdateRequest request);
}
