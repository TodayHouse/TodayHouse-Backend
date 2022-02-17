package com.todayhouse.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SimpleUser {
    private Long id;
    private String nickname;
    private String profileImage;
    private String introduction;
}
