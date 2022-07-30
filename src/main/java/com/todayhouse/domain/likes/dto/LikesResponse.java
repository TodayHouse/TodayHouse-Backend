package com.todayhouse.domain.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikesResponse {
    private long likesCount;
    private boolean liked;

}
