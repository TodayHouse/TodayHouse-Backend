package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSaveResponse {
    Long id;
    String email;
    List<String> role;

    public UserSaveResponse (User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRoles();
    }
}