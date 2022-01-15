package com.todayhouse.security.application;

import com.todayhouse.security.domian.dto.Login;
import com.todayhouse.security.domian.dto.Save;
import com.todayhouse.security.domian.user.User;

import java.util.Optional;

public interface UserService {
    User save(Save.UserSaveRequest request);
    Optional<User> findByEmail(String email);
    String login(Login.UserLoginRequest request);
}
