package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReplyGetResponse {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private Writer replyUserDto;
    private boolean liked;
    private int likesCount;
    private boolean isMine;

    @Builder
    public ReplyGetResponse(Long id, String content, LocalDateTime createdDate, User user, int likesCount) {

        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.replyUserDto = new Writer(user.getId(), user.getNickname(), user.getProfileImage());
        this.likesCount = likesCount;

    }

    @Getter
    @NoArgsConstructor
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
    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    public void IsMine(Long userId) {
        if (userId == null) {
            return;
        }
        this.isMine = userId.equals(this.replyUserDto.id);
    }
}
