package com.todayhouse.domain.user.api;

import com.todayhouse.domain.user.application.SellerService;
import com.todayhouse.domain.user.dto.request.SellerRequest;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService service;

    @PostMapping
    public BaseResponse saveSeller(@Valid @RequestBody SellerRequest request){
        return new BaseResponse(service.saveSellerRequest(request));
    }
}
