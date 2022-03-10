package com.todayhouse.infra.S3Storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    List<String> upload(List<MultipartFile> multipartFile);

    byte[] getImage(String fileName);

    void delete(List<String> fileName);

    void deleteOne(String fileName);
}
