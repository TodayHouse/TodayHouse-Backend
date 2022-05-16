package com.todayhouse.domain.story.dto.reqeust;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReplyRequest {
    private String content;
    private Long storyId;

}
