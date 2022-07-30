package com.todayhouse.domain.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnLikesResponse {

    private long likesCount;
    private Boolean liked;

}
