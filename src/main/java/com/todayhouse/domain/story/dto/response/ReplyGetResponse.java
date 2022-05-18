package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyGetResponse {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private Writer replyUserDto;

    public ReplyGetResponse(Long id, String content, LocalDateTime createdDate, User user) {

        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.replyUserDto = new Writer(user.getId(), user.getNickname(), user.getProfileImage());
    }

    @Data
    private static class Writer {
        private Long id;
        private String nickname;
        private String profileImage;

        public Writer(Long id, String nickname, String profileImage) {
            this.id = id;
            this.nickname = nickname;
            this.profileImage = profileImage;
        }
    }
}
