package com.todayhouse.domain.image.api;

import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final FileService fileService;

    @GetMapping(value = "/{file}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String file) {
        return new ResponseEntity<>(imageService.getImage(file), HttpStatus.OK);
    }

    @GetMapping
    public BaseResponse<List<String>> getAll() {
        return new BaseResponse<>(imageService.findStoryImageAll());
    }

    @DeleteMapping("/{file}")
    public BaseResponse<String> deleteOneFile(@PathVariable String file) {
        fileService.deleteOne(file);
        return new BaseResponse<>("File deleted : " + file);
    }

    @DeleteMapping
    public BaseResponse<String> deleteFiles(@RequestParam List<String> file) {
        fileService.delete(file);
        return new BaseResponse<>("File deleted : " + file);
    }
}
