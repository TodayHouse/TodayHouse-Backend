package com.todayhouse.domain.story.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCreateRequest {
    private String content;
    private Long storyId;

}
