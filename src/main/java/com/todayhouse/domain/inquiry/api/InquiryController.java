package com.todayhouse.domain.inquiry.api;

import com.todayhouse.domain.inquiry.application.InquiryService;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySaveRequest;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import com.todayhouse.domain.inquiry.dto.response.InquiryResponse;
import com.todayhouse.domain.user.application.UserService;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/inquires")
@AllArgsConstructor
public class InquiryController {
    private final UserService userService;
    private final InquiryService inquiryService;

    @PostMapping
    public BaseResponse<Long> saveInquiry(@Valid @RequestBody InquirySaveRequest request) {
        Inquiry inquiryRequest = Inquiry.builder()
                .isSecret(request.getIsSecret()).content(request.getContent()).category(request.getCategory())
                .build();
        Inquiry inquiry = inquiryService.saveInquiry(inquiryRequest, request.getProductId());
        return new BaseResponse(inquiry.getId());
    }

    @GetMapping
    public BaseResponse<PageDto<InquiryResponse>> findAllInquires(@ModelAttribute InquirySearchRequest inquirySearchRequest, Pageable pageable) {
        Page<Inquiry> inquiries = inquiryService.findAllInquiries(inquirySearchRequest, pageable);
        Long myId = getMyId();
        PageDto<InquiryResponse> inquiryResponse = new PageDto<>(inquiries.map(inquiry -> new InquiryResponse(inquiry, myId)));
        return new BaseResponse(inquiryResponse);
    }

    @DeleteMapping("/{inquiryId}")
    public BaseResponse deleteInquiry(@PathVariable Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return new BaseResponse<>("삭제되었습니다.");
    }

    private Long getMyId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email).orElse(null);
        if (ObjectUtils.isEmpty(user))
            return null;
        return user.getId();
    }
}
