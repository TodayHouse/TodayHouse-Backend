package com.todayhouse.infra.S3Storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.todayhouse.infra.S3Storage.exception.AmazonClientException;
import com.todayhouse.infra.S3Storage.exception.ImageFileIOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.todayhouse.global.error.BaseResponseStatus.AMAZON_CLIENT_EXCEPTION;
import static com.todayhouse.global.error.BaseResponseStatus.IMAGE_FILE_IO_EXCEPTION;

@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3Client;

    public S3Service(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String upload(MultipartFile multipartFile){
        File file = convertMultiPartFileToFile(multipartFile);
        String fileName = multipartFile.getOriginalFilename();
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
        } catch (RuntimeException e) {
            throw new AmazonClientException(AMAZON_CLIENT_EXCEPTION);
        }
        file.delete();
        return "File uploaded : " + fileName;
    }

    public String delete(String fileName){
        amazonS3Client.deleteObject(bucketName, fileName);
        return "File deleted : " + fileName;
    }

    // 입력받은 multipart file 을 file 로 변환하기 위한 메소드 입니다.
    private File convertMultiPartFileToFile(MultipartFile file){
        File convertedFile = new File(file.getOriginalFilename()); // 입력받은 multipart file 의 이름으로 file 을 생성합니다.
        try (FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        } catch (IOException e){
            throw new ImageFileIOException(IMAGE_FILE_IO_EXCEPTION);
        }
        return convertedFile;
    }
}
