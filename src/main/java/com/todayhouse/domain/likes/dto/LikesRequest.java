package com.todayhouse.domain.likes.dto;

import com.todayhouse.domain.likes.domain.LikesType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikesRequest {
    private LikesType likesType;
    private Long typeId;
}
