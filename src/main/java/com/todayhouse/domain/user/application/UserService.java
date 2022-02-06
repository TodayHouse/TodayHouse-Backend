package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;

public interface UserService {
    boolean existByEmail(String email);

    boolean existByNickname(String nickname);

    User saveUser(UserSignupRequest request);

    String login(UserLoginRequest request);
}
