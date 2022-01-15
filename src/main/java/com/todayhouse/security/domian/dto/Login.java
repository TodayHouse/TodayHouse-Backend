package com.todayhouse.security.domian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Login {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserLoginRequest {
        String email;
        String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserLoginResponse {
        String jwt;
    }
}
