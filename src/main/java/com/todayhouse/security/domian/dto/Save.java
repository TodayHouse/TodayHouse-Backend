package com.todayhouse.security.domian.dto;

import com.todayhouse.security.domian.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class Save {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSaveRequest {
        private String email;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSaveResponse {
        Long id;
        String email;
        List<String> role;

        public UserSaveResponse (User user){
            this.id = user.getId();
            this.email = user.getEmail();
            this.role = user.getRoles();
        }
    }
}
