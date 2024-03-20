package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.request.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.request.ReviewUpdateDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.service.review.ReviewInquiryService;
import com.nailshop.nailborhood.service.review.ReviewService;
import com.nailshop.nailborhood.dto.review.request.ReviewRegistrationRequestDto;
import com.nailshop.nailborhood.service.review.ReviewRegistrationService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewInquiryService reviewInquiryService;
    private final ReviewRegistrationService reviewRegistrationService;


    @Tag(name = "review", description = "review API")
    @Operation(summary = "리뷰 등록", description = "review API")
    // 매장내 리뷰 등록
    @PostMapping(consumes = {"multipart/form-data"}, value = "/{shopId}/review/registration")
    public ResponseEntity<ResultDto<Void>> registerShop(@PathVariable Long shopId,
                                                        @RequestHeader(AUTH) String accessToken,
                                                        @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data") ReviewRegistrationRequestDto reviewRegistrationRequestDto) {
        CommonResponseDto<Object> commonResponseDto = reviewRegistrationService.registerReview(shopId,accessToken,multipartFileList,reviewRegistrationRequestDto );
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .body(resultDto);
    }

    //리뷰 수정
    @PostMapping(consumes = {"multipart/form-data"}, value = "/review/update/{reviewId}")
    public String reviewUpdate(
//                               @RequestHeader(AUTH) String accessToken,
                               @PathVariable Long reviewId,
                               @RequestParam(value = "shopId") Long shopId,
                               @RequestPart(value = "img") List<MultipartFile> multipartFileList,
                               @ModelAttribute ReviewUpdateDto reviewUpdateDto,
                               RedirectAttributes redirectAttributes){

        try {
            CommonResponseDto<Object> commonResponseDto = reviewService.reviewUpdate(reviewId, shopId, multipartFileList,reviewUpdateDto);
            ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/review/inquiry/{reviewId}";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.REVIEW_MODIFY_FAIL.getDescription());

            return "review/review_mod";

        }
    }

    // 리뷰 수정 뷰
    @GetMapping("/review/update/{reviewId}")
    public String getReviewUpdate(Model model,
                                  @PathVariable Long reviewId,
                                  @RequestParam(value = "shopId") Long shopId
                                   /*,ReviewUpdateDto reviewUpdateDto
                                  @RequestPart(value = "img") List<MultipartFile> multipartFileList,
                                  @RequestPart(value = "data") ReviewUpdateDto reviewUpdateDto*/){

        CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, shopId);
        ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
        resultDto.setData((ReviewDetailResponseDto) detailReview.getData());

        model.addAttribute("result", resultDto);
//        model.addAttribute("result", resultDto);

        return "review/review_mod";
    }

    // 리뷰 신고
    @Tag(name = "review", description = "review API")
    @Operation(summary = "리뷰 신고", description = "review API")
    @PostMapping("/review/report/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewReport(@RequestHeader(AUTH) String accessToken,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId,
                                                        @RequestBody ReviewReportDto reviewReportDto){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewReport(accessToken, reviewId, shopId,reviewReportDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    // 리뷰 삭제
    @Tag(name = "myPage", description = "myPage API")
    @Operation(summary = "리뷰 삭제", description = "myPage API")
    @DeleteMapping("/mypage/review/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewDelete(@RequestHeader(AUTH) String accessToken,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewDelete(accessToken, reviewId, shopId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }


}
