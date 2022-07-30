package com.todayhouse.domain.likes.application;

import com.todayhouse.domain.likes.domain.LikesType;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.LikesResponse;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesResponse;
import com.todayhouse.domain.user.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface LikesService {
    LikesResponse likes(@AuthenticationPrincipal User principal, LikesRequest request);

    UnLikesResponse unlikes(@AuthenticationPrincipal User principal, UnLikesRequest request);

    boolean isMatching(LikesType likesType);
}
