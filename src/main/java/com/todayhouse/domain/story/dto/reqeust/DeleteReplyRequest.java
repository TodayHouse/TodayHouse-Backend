package com.todayhouse.domain.story.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteReplyRequest {
    private  Long storyId;
}