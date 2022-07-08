package com.todayhouse.domain.inquiry.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.inquiry.domain.Answer;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryResponse {
    private Long id;
    private boolean isBuy;
    private boolean isAnswered;
    private String category;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String answer;
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String answerName;
    private LocalDateTime inquiryCreatedAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime answerCreatedAt;

    public InquiryResponse(Inquiry inquiry, Long myId) {
        boolean isValid = isValidContent(inquiry, myId);
        this.id = inquiry.getId();
        this.isBuy = inquiry.isBuy();
        this.isAnswered = false;
        this.category = inquiry.getCategory();
        this.content = isValid ? inquiry.getContent() : "비밀글입니다.";
        this.userName = getValidUserName(inquiry.getUser(), myId);
        this.inquiryCreatedAt = inquiry.getCreatedAt();

        if (inquiry.getAnswer() != null) {
            Answer answer = inquiry.getAnswer();
            this.isAnswered = true;
            this.answer = isValid ? answer.getContent() : "비밀글입니다.";
            this.answerName = answer.getName();
            this.answerCreatedAt = answer.getCreatedAt();
        }
    }

    private boolean isValidContent(Inquiry inquiry, Long myId) {
        return inquiry.getUser().getId() == myId || !inquiry.isSecret();
    }

    private String getValidUserName(User user, Long myId) {
        String userName = user.getNickname();
        if (user.getId() == myId)
            return userName;
        StringBuilder builder = new StringBuilder(userName);
        for (int i = builder.length() - 1; i > builder.length() / 2; i--) {
            builder.setCharAt(i, '*');
        }
        return String.valueOf(builder);
    }
}
