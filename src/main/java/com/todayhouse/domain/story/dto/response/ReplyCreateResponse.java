package com.todayhouse.domain.story.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReplyCreateResponse {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private Boolean isMine;
    private String nickname;

    @Builder
    public ReplyCreateResponse(Long id, String content, LocalDateTime createdDate, Boolean isMine, String nickname) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.isMine = isMine;
        this.nickname = nickname;
    }
}
