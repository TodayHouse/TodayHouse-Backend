package com.todayhouse.domain.user.application;

import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceImplTest extends IntegrationBase {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    void 이메일_중복(){
        UserSignupRequest request1 = UserSignupRequest.builder()
                .email("today.house.clone@gmail.com")
                .password1("09876543")
                .password2("09876543")
                .nickname("test")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();

        UserSignupRequest request2 = UserSignupRequest.builder()
                .email("today.house.clone@gmail.com")
                .password1("09876543")
                .password2("09876543")
                .nickname("pass")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();

        userRepository.save(request1.toEntity());
        assertThrows(IllegalArgumentException.class,()->userService.saveUser(request2));
    }

    @Test
    void 닉네임_중복(){
        UserSignupRequest request1 = UserSignupRequest.builder()
                .email("today.house.clone@gmail.com")
                .password1("09876543")
                .password2("09876543")
                .nickname("test")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();

        UserSignupRequest request2 = UserSignupRequest.builder()
                .email("pass@gmail.com")
                .password1("09876543")
                .password2("09876543")
                .nickname("test")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();

        userRepository.save(request1.toEntity());
        assertThrows(IllegalArgumentException.class,()->userService.saveUser(request2));
    }
}