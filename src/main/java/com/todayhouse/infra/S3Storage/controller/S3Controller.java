package com.todayhouse.infra.S3Storage.controller;

import com.todayhouse.infra.S3Storage.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(s3Service.upload(file), HttpStatus.OK);
    }

    @DeleteMapping("{file}")
    public ResponseEntity<String> deleteFile(@PathVariable String file){
        return new ResponseEntity<>(s3Service.delete(file), HttpStatus.OK);
    }
}
