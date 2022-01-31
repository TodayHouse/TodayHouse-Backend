package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.dto.request.UserSaveRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    void 이메일_중복(){
        UserSaveRequest request1 = UserSaveRequest.builder()
                .authProvider(AuthProvider.local)
                .email("today.house.clone@gmail.com")
                .password1("09876543")
                .password2("09876543")
                .nickname("test")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();

        UserSaveRequest request2 = UserSaveRequest.builder()
                .authProvider(AuthProvider.local)
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
        UserSaveRequest request1 = UserSaveRequest.builder()
                .authProvider(AuthProvider.local)
                .email("today.house.clone@gmail.com")
                .password1("09876543")
                .password2("09876543")
                .nickname("test")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();

        UserSaveRequest request2 = UserSaveRequest.builder()
                .authProvider(AuthProvider.local)
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