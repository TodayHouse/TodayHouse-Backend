package com.todayhouse.infra.S3Storage.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @InjectMocks
    FileServiceImpl fileService;

    @Mock
    AmazonS3 amazonS3;

    @Test
    @DisplayName("이미지 업로드")
    void upload() {
        MultipartFile multipartFile = new MockMultipartFile("data", "filename.txt", "text/plain", "bytes".getBytes());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);
        when(amazonS3.putObject(any())).thenReturn(any());

        List<String> fileNames = fileService.upload(list);

        String result = fileNames.get(0);
        UUID.randomUUID().toString().concat(result.substring(result.lastIndexOf(".")));
        assertThat(fileNames.size()).isEqualTo(1);
        assertThat(fileNames.get(0)).isEqualTo(result);
    }
}