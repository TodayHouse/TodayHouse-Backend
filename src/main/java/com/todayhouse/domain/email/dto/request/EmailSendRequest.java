package com.todayhouse.domain.email.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailSendRequest {
    String email;
}
