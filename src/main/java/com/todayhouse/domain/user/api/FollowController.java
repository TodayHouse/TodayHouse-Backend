package com.todayhouse.domain.user.api;

import com.todayhouse.domain.user.application.FollowService;
import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.dto.SimpleUser;
import com.todayhouse.domain.user.dto.request.FollowRequest;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping
    public BaseResponse saveFollow(@Valid @RequestBody FollowRequest request) {
        Follow follow = followService.saveFollow(request.getFromId(), request.getToId());
        return new BaseResponse(follow);
    }

    @DeleteMapping
    public BaseResponse deleteFollow(@RequestParam Long fromId, @RequestParam Long toId) {
        followService.deleteFollow(fromId, toId);
        return new BaseResponse();
    }

    @GetMapping("/followers/count/{id}")
    public BaseResponse countFollowers(@PathVariable Long id) {
        long count = followService.countFollowers(id);
        return new BaseResponse(count);
    }

    @GetMapping("/followings/count/{id}")
    public BaseResponse countFollowings(@PathVariable Long id) {
        long count = followService.countFollowings(id);
        return new BaseResponse(count);
    }

    @GetMapping("/followers/{id}")
    public BaseResponse findFollowers(@PathVariable Long id) {
        Set<SimpleUser> followers = followService.findFollowers(id);
        return new BaseResponse(followers);
    }

    @GetMapping("/followings/{id}")
    public BaseResponse findFollowings(@PathVariable Long id) {
        Set<SimpleUser> followings = followService.findFollowings(id);
        return new BaseResponse(followings);
    }

    @GetMapping
    public BaseResponse isFollowing(@RequestParam Long fromId, @RequestParam Long toId) {
        return new BaseResponse(followService.isFollowing(fromId, toId));
    }
}