package com.todayhouse.domain.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FollowRequest {
    @NotNull
    private Long fromId;

    @NotNull
    private Long toId;
}