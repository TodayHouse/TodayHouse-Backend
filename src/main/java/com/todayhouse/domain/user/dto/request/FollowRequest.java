package com.todayhouse.domain.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FollowRequest {
    @NotNull(message = "fromId를 입력해주세요")
    private Long fromId;

    @NotNull(message = "toId를 입력해주세요")
    private Long toId;
}