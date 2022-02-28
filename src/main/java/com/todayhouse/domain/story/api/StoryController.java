package com.todayhouse.domain.story.api;

import com.todayhouse.domain.story.application.StoryService;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
public class StoryController {

    private final StoryService storyService;

    @PostMapping
    public BaseResponse<Long> create(@RequestPart(value = "file", required = false) List<MultipartFile> multipartFile,
                                     @RequestPart(value = "request") StoryCreateRequest request){
        return new BaseResponse<>(storyService.save(multipartFile, request));
    }

    @GetMapping("/{id}")
    public BaseResponse<StoryGetDetailResponse> findById(@PathVariable Long id){
        return new BaseResponse<>(storyService.findById(id));
    }

    @GetMapping
    public BaseResponse<List<StoryGetListResponse>> findAllDesc(){
        return new BaseResponse<>(storyService.findAllDesc());
    }

    @GetMapping("/{id}/images")
    public BaseResponse<List<String>> getImageInStory(@PathVariable Long id){
        return new BaseResponse<>(storyService.getImageInStory(id));
    }

    @PatchMapping("/{id}")
    public BaseResponse<Long> update(@PathVariable Long id, @RequestBody StoryUpdateRequest request){
        return new BaseResponse<>(storyService.update(id, request));
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> delete(@PathVariable Long id){
        storyService.delete(id);
        return new BaseResponse<>("해당 스토리가 삭제되었습니다.");
    }
}
