package com.todayhouse.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), GUEST("ROLE_GUEST");

    private final String key;

    // authentication의 authority를 role로 변환
    public static Role grantedAuthorityToRole(String key) {
        for (Role r : Role.values()) {
            if(r.getKey().equals(key))
                return r;
        }
        return null;
    }
}
