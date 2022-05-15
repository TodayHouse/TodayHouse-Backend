package com.todayhouse.domain.story.dto.reqeust;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteReplyRequest {
    private final UUID id;
}
