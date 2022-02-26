package com.todayhouse.infra.S3Storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.todayhouse.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.todayhouse.global.error.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class FileUploadServiceImpl implements FileUploadService{

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    @Override
    public List<String> upload(List<MultipartFile> multipartFile){
        List<String> fileNameList = new ArrayList<>();

        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e){
                throw new BaseException(IMAGE_FILE_IO_EXCEPTION);
            }
            fileNameList.add(fileName);
        });
        return fileNameList;
    }

    private String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName){
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new BaseException(INVALID_FILE_EXTENSION_EXCEPTION);
        }
    }
}
