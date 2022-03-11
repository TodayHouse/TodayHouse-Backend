package com.todayhouse.domain.image.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageResponse {
    private String fileName;
    private byte[] image;
}
