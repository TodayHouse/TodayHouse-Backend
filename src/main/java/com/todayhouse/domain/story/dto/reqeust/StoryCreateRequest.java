package com.todayhouse.domain.story.dto.reqeust;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class StoryCreateRequest {
    @Length(min = 1, max = 50, message = "제목은 1자 이상 50자 이하로 입력해주세요.")
    private String title;
    private String content;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Story.Category category;

    public Story toEntity(User user) {
        return Story.builder()
                .title(title)
                .content(content)
                .liked(0)
                .category(category)
                .user(user)
                .build();
    }
}
/*
SecurityContextHolder.getContext().getAuthentication().getName()
 */