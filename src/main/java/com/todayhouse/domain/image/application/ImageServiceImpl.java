package com.todayhouse.domain.image.application;

import com.todayhouse.domain.image.dao.ImageRepository;
import com.todayhouse.domain.image.domain.Image;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Override
    public void save(List<String> fileName, Story story){
        imageRepository.saveAll(fileName
                .stream()
                .map(file -> new Image(file, story))
                .collect(Collectors.toList()));
    }

    @Override
    public byte[] getImage(String fileName){
        try {
            return fileService.getImage(fileName);
        } catch (IOException e) {
            throw new BaseException(BaseResponseStatus.IMAGE_FILE_IO_EXCEPTION);
        }
    }

    @Override
    public void deleteOne(String fileName){
        fileService.deleteOne(fileName);
    }

    @Override
    public void delete(List<String> fileName){
        fileService.delete(fileName);
    }

    @Override
    public String getThumbnailUrl(Story story){
        Image image = imageRepository.findFirstByStoryOrderByCreatedAtDesc(story).orElseGet(null);
        if (image == null) return null;
        return image.getFileName();
    }

    @Override
    public List<String> findAll(){
        return imageRepository.findAll().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());
    }
}
