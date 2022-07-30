package com.todayhouse.domain.likes.api;

import com.todayhouse.domain.likes.application.LikesService;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.LikesResponse;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesResponse;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikesController {
    private final List<LikesService> likesServices;

    @PostMapping("/likes")
    public BaseResponse<LikesResponse> likes(@AuthenticationPrincipal User user, @RequestBody LikesRequest request) {
        for (LikesService likesService : likesServices) {
            if (likesService.isMatching(request.getLikesType())) {
                LikesResponse likes = likesService.likes(user, request);
                return new BaseResponse<>(likes);
            }
        }
        throw new RuntimeException();
    }

    @DeleteMapping("/likes")
    public BaseResponse<UnLikesResponse> unlikes(@AuthenticationPrincipal User user, @RequestBody UnLikesRequest request) {
        for (LikesService likesService : likesServices) {
            if (likesService.isMatching(request.getLikesType())) {
                UnLikesResponse unlikes = likesService.unlikes(user, request);
                return new BaseResponse<>(unlikes);
            }
        }

        throw new RuntimeException();
    }
}
