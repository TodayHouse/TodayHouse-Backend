package com.todayhouse.domain.image.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageResponse {
    private String fileName;
    private byte[] image;
}
