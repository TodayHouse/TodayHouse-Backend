package com.todayhouse.domain.likes.dto;

import com.todayhouse.domain.likes.domain.LikesType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnLikesRequest {

    private LikesType likesType;
    private Long typeId;
}
