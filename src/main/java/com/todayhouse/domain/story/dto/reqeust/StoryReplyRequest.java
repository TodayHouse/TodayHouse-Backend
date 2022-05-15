package com.todayhouse.domain.story.dto.reqeust;

import lombok.Data;

@Data
public class StoryReplyRequest {
    private String content;
    private Long StoryId;
}
