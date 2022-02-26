package com.todayhouse.infra.S3Storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    List<String> upload(List<MultipartFile> multipartFile);
}
