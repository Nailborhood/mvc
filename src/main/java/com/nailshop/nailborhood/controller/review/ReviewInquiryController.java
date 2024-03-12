package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.mypage.MyReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.service.review.ReviewInquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/nailborhood")
public class ReviewInquiryController {

    private final ReviewInquiryService reviewInquiryService;


    // 리뷰 상세 조회
    @Tag(name = "review", description = "review API")
    @Operation(summary = "리뷰 상세 조회", description = "review API")
    @GetMapping("user/review/inquiry/{reviewId}")
    public String detailReview(@PathVariable Long reviewId,
                               @RequestParam(value = "shopId") Long shopId,
                               Model model){
        CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, shopId);
        ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
        resultDto.setData((ReviewDetailResponseDto) detailReview.getData());

        model.addAttribute("result", resultDto);

        return "review/review_detail";
    }

    // 리뷰 전체 조회
    @Tag(name = "review", description = "review API")
    @Operation(summary = "리뷰 전체 조회", description = "review API")
    @GetMapping("/review/inquiry")
    public ResponseEntity<ResultDto<ReviewListResponseDto>> allReview(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                      @RequestParam(value = "sortBy", defaultValue = "likeCnt", required = false) String sortBy,
                                                                      @RequestParam(value = "category", defaultValue = "", required = false) String category){
        CommonResponseDto<Object> allReview = reviewInquiryService.allReview(page, size, sortBy, category);
        ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(allReview.getStatus(), allReview.getMessage());
        resultDto.setData((ReviewListResponseDto) allReview.getData());

        return ResponseEntity.status(allReview.getHttpStatus()).body(resultDto);
    }

}
