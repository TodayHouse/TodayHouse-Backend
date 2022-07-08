package com.todayhouse.domain.story.api;

import com.todayhouse.domain.story.application.StoryService;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static java.util.Objects.isNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
public class StoryController {

    private final StoryService storyService;

    @PostMapping
    public BaseResponse<Long> saveStory(@RequestPart(value = "file", required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "request") @Valid StoryCreateRequest request) {
        return new BaseResponse<>(storyService.saveStory(multipartFile, request));
    }

    @PostMapping("/{id}/image")
    public BaseResponse<Long> saveImage(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                        @PathVariable Long id) {
        return new BaseResponse<>(storyService.saveImage(multipartFile, id));
    }

    @GetMapping
    public BaseResponse<Page<StoryGetListResponse>> findAllDesc(Pageable pageable, StorySearchRequest request) {
        return new BaseResponse<>(storyService.searchStory(request, pageable));
    }

    @GetMapping("/{id}")
    public BaseResponse<StoryGetDetailResponse> findById(@PathVariable Long id) {
        return new BaseResponse<>(storyService.findById(id));
    }

    @GetMapping("/user")
    public BaseResponse<Slice<StoryGetListResponse>> findByUser(@RequestParam(required = false) String nickname,
                                                                @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (isNull(nickname)) return new BaseResponse<>(storyService.findByUser(pageable));
        else return new BaseResponse<>(storyService.findByUserNickname(nickname, pageable));
    }

    @GetMapping("/images")
    public BaseResponse<List<String>> getStoryImageFileNamesAll() {
        return new BaseResponse<>(storyService.getStoryImageFileNamesAll());
    }

    @GetMapping("/{id}/images")
    public BaseResponse<List<String>> getImageInStory(@PathVariable Long id) {
        return new BaseResponse<>(storyService.getImageInStory(id));
    }

    @GetMapping(value = "/images/{file}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String file) {
        return new ResponseEntity<>(storyService.getImage(file), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public BaseResponse<Long> update(@PathVariable Long id, @Valid @RequestBody StoryUpdateRequest request) {
        return new BaseResponse<>(storyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<String> delete(@PathVariable Long id) {
        storyService.deleteStory(id);
        return new BaseResponse<>("해당 스토리가 삭제되었습니다.");
    }

    @DeleteMapping("/images")
    public BaseResponse<String> deleteImages(@RequestParam List<String> file) {
        storyService.deleteImages(file);
        return new BaseResponse<>("File deleted : " + file);
    }
}
