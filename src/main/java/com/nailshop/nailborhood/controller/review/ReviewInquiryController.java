package com.nailshop.nailborhood.controller.review;


import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.mypage.MyReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.alarm.AlarmService;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.review.ReviewInquiryService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReviewInquiryController {

    private final ReviewInquiryService reviewInquiryService;
    private final AlarmService alarmService;
    private final MemberService memberService;
    private final CategoryRepository categoryRepository;



    // 리뷰 상세 조회
    @GetMapping("/review/inquiry/{reviewId}")
    public String detailReview(Model model,
                               Authentication authentication,
                               @AuthenticationPrincipal MemberDetails memberDetails,
                               @PathVariable Long reviewId,
                               @RequestParam(value = "shopId") Long shopId,
                               @RequestParam(required = false) Boolean alarmSent){

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
            String nickName = sessionDto.getNickname();
            model.addAttribute("sessionDto", sessionDto);
            model.addAttribute("nickName", nickName);
            CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, shopId, sessionDto.getId());
            ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
            resultDto.setData((ReviewDetailResponseDto) detailReview.getData());
            model.addAttribute("result", resultDto);
        } else {
            CommonResponseDto<Object> detailReview = reviewInquiryService.detailReviewForGuest(reviewId, shopId);
            ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
            resultDto.setData((ReviewDetailResponseDto) detailReview.getData());
            model.addAttribute("result", resultDto);
            model.addAttribute("sessionDto", "");
            model.addAttribute("nickName", "");
        }
        // 알람
        Member receiver = alarmService.getOwnerInfo(shopId);
        if (Boolean.TRUE.equals(alarmSent)) {
            model.addAttribute("alarmSent", true);
        }

        // 리뷰 작성자
        //Member reviewReceiver = alarmService.getUserInfo(reviewId);
        model.addAttribute("receiver", receiver);
        //model.addAttribute("reviewReceiver",reviewReceiver);

        return "review/review_detail";
    }

    // 리뷰 전체 조회 검색 통합
    @GetMapping("/review/inquiry")
    public String allReview(Model model,
                            Authentication authentication,
                            @AuthenticationPrincipal MemberDetails memberDetails,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                            @RequestParam(value = "orderby", defaultValue = "likeCnt", required = false) String criteria,
                            @RequestParam(value = "category", defaultValue = "", required = false) String category) {

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        boolean error = false;

        try {
            CommonResponseDto<Object> allReview = reviewInquiryService.allReview(keyword, page, size, criteria, category);
            ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(allReview.getStatus(), allReview.getMessage());
            resultDto.setData((ReviewListResponseDto) allReview.getData());

            List<Map<String, String>> criteriaOptions = reviewInquiryService.createCriteriaOptions();
            List<Category> categoryList = categoryRepository.findAll();

            model.addAttribute("result", resultDto);
            model.addAttribute("orderby", criteria);
            model.addAttribute("size", size);
            model.addAttribute("criteriaOptions", criteriaOptions);
            model.addAttribute("categories", categoryList);
            model.addAttribute("error", error);

        } catch (Exception e) {
            error = true;
//            model.addAttribute("error", ErrorCode.REVIEW_NOT_FOUND);
            model.addAttribute("error", error);

        }

        return "review/review_list";
    }

    @GetMapping("/review/category/inquiry")
    public ResponseEntity<ResultDto<ReviewListResponseDto>> allReviewByCategory(@RequestParam(value = "keyword", required = false) String keyword,
                                                                                @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                                                                @RequestParam(value = "orderby", defaultValue = "likeCnt", required = false) String criteria,
                                                                                @RequestParam(value = "category", defaultValue = "", required = false) String category) {

        CommonResponseDto<Object> allReview = reviewInquiryService.allReview(keyword, page, size, criteria, category);
        ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(allReview.getStatus(), allReview.getMessage());
        resultDto.setData((ReviewListResponseDto) allReview.getData());

        return ResponseEntity.status(allReview.getHttpStatus()).body(resultDto);

    }
}