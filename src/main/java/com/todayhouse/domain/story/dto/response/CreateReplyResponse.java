package com.todayhouse.domain.story.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateReplyResponse implements Serializable {
    private final Long id;
    private final String content;
    private final LocalDateTime createdDate;
    private final Boolean isMine;
    private final String nickname;

}
