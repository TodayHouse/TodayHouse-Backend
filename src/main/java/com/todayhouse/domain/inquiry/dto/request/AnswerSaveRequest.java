package com.todayhouse.domain.inquiry.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnswerSaveRequest {
    @NotNull(message = "productId를 입력해주세요.")
    Long productId;
    @NotNull(message = "inquiryId를 입력해주세요.")
    Long inquiryId;
    @Size(min = 1, message = "1~200자까지 입력 가능합니다.")
    @Size(max = 200, message = "1~200자까지 입력 가능합니다.")
    String content;
}
