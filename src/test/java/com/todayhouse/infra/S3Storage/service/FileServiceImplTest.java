package com.todayhouse.infra.S3Storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @InjectMocks
    FileServiceImpl fileService;

    @Mock
    AmazonS3 amazonS3;

    @Test
    @DisplayName("이미지 여러개 업로드")
    void upload() {
        MultipartFile multipartFile = new MockMultipartFile("data", "filename.txt", "text/plain", "bytes".getBytes());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);
        when(amazonS3.putObject(any())).thenReturn(any());

        List<String> fileNames = fileService.uploadImages(list);

        assertThat(fileNames.size()).isEqualTo(1);
        assertThat(fileNames.get(0).length()).isEqualTo(40);
    }

    @Test
    @DisplayName("이미지 하나 업로드")
    void uploadOne() {
        MultipartFile multipartFile = new MockMultipartFile("data", "filename.txt", "text/plain", "bytes".getBytes());
        when(amazonS3.putObject(any())).thenReturn(any());

        String fileName = fileService.uploadImage(multipartFile);

        assertThat(fileName.length()).isEqualTo(40);
    }

    @Test
    @DisplayName("이미지 여러개 삭제")
    void delete() {
        List<String> imgs = List.of("img1", "img2", "img3");
        DeleteObjectsResult mock = Mockito.mock(DeleteObjectsResult.class);
        ReflectionTestUtils.setField(fileService, "bucketName", "bucket");
        when(amazonS3.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(mock);

        fileService.delete(imgs);
        verify(amazonS3).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("이미지 하나 삭제")
    void deleteOne() {
        String file = "img";
        ReflectionTestUtils.setField(fileService, "bucketName", "bucket");
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());

        fileService.deleteOne(file);
        verify(amazonS3).deleteObject("bucket", file);
    }

    @Test
    @DisplayName("파일 이름 url로 변경")
    void changeToUrl() throws MalformedURLException {
        String file = "aa.jpg";
        String url = "https://bucket-aa.jpg";
        ReflectionTestUtils.setField(fileService, "bucketName", "bucket");
        when(amazonS3.getUrl("bucket", file)).thenReturn(new URL(url));

        String result = fileService.changeFileNameToUrl(file);
        assertThat(result).isEqualTo(url);
    }
}