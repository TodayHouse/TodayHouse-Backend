package com.todayhouse.domain.user.oauth.application;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;


public interface OAuthService {
    String findNicknamebyEmail(String email);

    String provideToken(String email);

    User saveGuest(OAuthSignupRequest request);
}
