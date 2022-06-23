package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.domain.user.dto.response.UserLoginResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    boolean existByEmail(String email);

    boolean existByNickname(String nickname);

    User saveUser(UserSignupRequest request);

    UserLoginResponse login(UserLoginRequest request);

    void updatePassword(PasswordUpdateRequest request);

    void updateUserInfo(MultipartFile profileImg, User request);
}
