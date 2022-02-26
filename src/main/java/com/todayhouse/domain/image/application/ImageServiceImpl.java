package com.todayhouse.domain.image.application;

import com.todayhouse.domain.image.dao.ImageRepository;
import com.todayhouse.domain.image.domain.Image;
import com.todayhouse.domain.story.domain.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;

    @Override
    public void save(List<String> fileName, Story story){
        imageRepository.saveAll(fileName
                .stream()
                .map(file -> new Image(file, story))
                .collect(Collectors.toList()));
    }
}
