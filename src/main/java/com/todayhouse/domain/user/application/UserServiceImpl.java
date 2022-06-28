package com.todayhouse.domain.user.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.*;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.domain.user.dto.response.UserLoginResponse;
import com.todayhouse.domain.user.exception.*;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final FileService fileService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByEmail(String email) {
        return userRepository.existsByEmailAndNicknameIsNotNull(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public User saveUser(UserSignupRequest request) {
        if (emailVerificationTokenRepository.findByEmailAndExpired(request.getEmail(), true)
                .isEmpty()) {
            throw new UserEmailNotAuthException();
        }
        // 중복 회원가입, request 유효성 검사
        validateSignupRequest(request);
        // OAuth 인증만 받고 가입하지 않은 이메일은 업데이트
        return saveOrUpdateUser(request);
    }

    @Override
    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }
        String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
        return UserLoginResponse.builder().id(user.getId()).accessToken(jwt).build();
    }

    @Override
    public void updatePassword(PasswordUpdateRequest request) {
        if (!request.getPassword1().equals(request.getPassword2()))
            throw new SignupPasswordException();
        User user = getValidUser();
        user.updatePassword(request.getPassword1());
    }

    @Override
    public void updateUserInfo(MultipartFile profileImg, User request) {
        checkNicknameDuplication(request.getNickname());
        User user = getValidUser();
        updateUserProfile(profileImg, request);
        deleteUserProfile(user);
        user.updateUserInfo(request);
    }

    private void validateSignupRequest(UserSignupRequest request) {
        if (userRepository.existsByEmailAndNicknameIsNotNull(request.getEmail()))
            throw new UserEmailExistExcecption();

        checkNicknameDuplication(request.getNickname());

        if (!request.getPassword1().equals(request.getPassword2()))
            throw new SignupPasswordException();
    }

    private User saveOrUpdateUser(UserSignupRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(u -> {
                    u.updateUser(request.toEntity());
                    return u;
                }).orElseGet(() -> userRepository.save(request.toEntity()));
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private void checkNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname))
            throw new UserNicknameExistException();
    }

    private void updateUserProfile(MultipartFile newImg, User user) {
        if (ObjectUtils.isEmpty(newImg))
            return;
        String newImgUrl = fileService.changeFileNameToUrl(fileService.uploadImage(newImg));
        user.updateProfileImage(newImgUrl);
    }

    private void deleteUserProfile(User user) {
        if (ObjectUtils.isEmpty(user.getProfileImage()))
            return;
        String oldImgUrl = fileService.changeUrlToFileName(user.getProfileImage());
        fileService.deleteOne(oldImgUrl);
    }

    //테스트 계정
    @PostConstruct
    private void preMember() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty())
            return;
        Seller seller = Seller.builder().brand("admin_brand").companyName("admin").build();

        User user = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@admin.com")
                .password(new BCryptPasswordEncoder().encode("today123"))
                .roles(Collections.singletonList(Role.ADMIN))
                .agreement(Agreement.agreeAll())
                .nickname("admin")
                .seller(seller)
                .build());

        userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("a@a.com")
                .password(new BCryptPasswordEncoder().encode("abc12345"))
                .roles(Collections.singletonList(Role.USER))
                .agreement(Agreement.agreeAll())
                .nickname("testuser1")
                .build());


    }
}
