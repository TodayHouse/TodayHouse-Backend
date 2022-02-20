package com.todayhouse.domain.user.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SimpleUser {
    private Long id;
    private String nickname;
    private String profileImage;
    private String introduction;
}
