package com.todayhouse.domain.inquiry.api;

import com.todayhouse.domain.inquiry.application.AnswerService;
import com.todayhouse.domain.inquiry.application.InquiryService;
import com.todayhouse.domain.inquiry.domain.Answer;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.AnswerSaveRequest;
import com.todayhouse.domain.inquiry.dto.request.InquirySaveRequest;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import com.todayhouse.domain.inquiry.dto.response.InquiryResponse;
import com.todayhouse.domain.user.application.UserService;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/inquires")
@RequiredArgsConstructor
public class InquiryController {
    private final UserService userService;
    private final AnswerService answerService;
    private final InquiryService inquiryService;

    @PostMapping
    public BaseResponse<Long> saveInquiry(@Valid @RequestBody InquirySaveRequest request) {
        Inquiry inquiryRequest = Inquiry.builder().isSecret(request.getIsSecret()).content(request.getContent()).category(request.getCategory()).build();
        Inquiry inquiry = inquiryService.saveInquiry(inquiryRequest, request.getProductId());
        return new BaseResponse(inquiry.getId());
    }

    @GetMapping
    public BaseResponse<PageDto<InquiryResponse>> findAllInquires(@ModelAttribute InquirySearchRequest request, Pageable pageable) {
        User myUser = getUser();
        Long myId = myUser == null ? null : myUser.getId();
        Page<Inquiry> inquiries = inquiryService.findAllInquiries(request, pageable);
        PageDto<InquiryResponse> inquiryResponse = new PageDto<>(inquiries.map(inquiry -> new InquiryResponse(inquiry, myId)));
        return new BaseResponse(inquiryResponse);
    }

    @DeleteMapping("/{inquiryId}")
    public BaseResponse deleteInquiry(@PathVariable Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return new BaseResponse<>("삭제되었습니다.");
    }

    @PostMapping("/answer")
    public BaseResponse<Long> saveAnswer(@RequestBody AnswerSaveRequest request) {
        String sellerName = getUser().getNickname();
        Answer answer = Answer.builder().content(request.getContent()).name(sellerName).build();
        Answer save = answerService.saveAnswer(answer, request.getProductId(), request.getInquiryId());
        return new BaseResponse(save.getId());
    }

    @DeleteMapping("/answer/{answerId}")
    public BaseResponse<Long> deleteAnswer(@PathVariable Long answerId, @RequestParam("productId") Long productId) {
        answerService.deleteAnswer(answerId, productId);
        return new BaseResponse("삭제되었습니다.");
    }

    private User getUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email).orElse(null);
    }
}
