package com.todayhouse.domain.user.api;

import com.todayhouse.domain.user.application.SellerService;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.dto.request.SellerRequest;
import com.todayhouse.domain.user.dto.response.SellerResponse;
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
    public BaseResponse saveSeller(@Valid @RequestBody SellerRequest request) {
        Seller seller = service.saveSellerRequest(request);
        return new BaseResponse(new SellerResponse(seller));
    }

    @GetMapping("/{id}")
    public BaseResponse findSeller(@PathVariable Long id) {
        Seller seller = service.findSeller(id);
        return new BaseResponse(new SellerResponse(seller));
    }
}
