package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.request.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.request.ReviewUpdateDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.review.ReviewInquiryService;
import com.nailshop.nailborhood.service.review.ReviewService;
import com.nailshop.nailborhood.dto.review.request.ReviewRegistrationRequestDto;
import com.nailshop.nailborhood.service.review.ReviewRegistrationService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewInquiryService reviewInquiryService;
    private final ReviewRegistrationService reviewRegistrationService;
    private final ShopDetailService shopDetailService;
    private final CategoryRepository categoryRepository;

    // 리뷰 등록(GET)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_OWNER')")
    @GetMapping("/{shopId}/review/registration")
    public String showRegisterReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                     Model model,
                                     @PathVariable Long shopId){

        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";

        List<Category> categoryList = categoryRepository.findAll();

        model.addAttribute("shopId", shopId);
        model.addAttribute("categories", categoryList);
        model.addAttribute("memberNickname", nicknameSpace);

        return "review/review_registration";
    }

    // 리뷰 등록(POST)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_USER')")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/{shopId}/review/registration")
    public String registerReview(@PathVariable Long shopId,
                                 @AuthenticationPrincipal MemberDetails memberDetails,
                                 @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                 @ModelAttribute ReviewRegistrationRequestDto reviewRegistrationRequestDto,
                                 RedirectAttributes redirectAttributes) {

        try {
            CommonResponseDto<Object> commonResponseDto = reviewRegistrationService.registerReview(shopId, memberDetails, multipartFileList, reviewRegistrationRequestDto);
            ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/review/inquiry";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.REVIEW_REGISTER_FAIL);

            return "review/review_registration";
        }
    }

    // 리뷰 수정
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

            redirectAttributes.addAttribute("reviewId", reviewId);
            redirectAttributes.addAttribute("shopId", shopId);

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

        return "review/review_mod";
    }

    // 리뷰 신고
    @PostMapping("/review/report/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewReport(/*@RequestHeader(AUTH) String accessToken,*/
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId,
                                                        @RequestBody ReviewReportDto reviewReportDto){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewReport(/*accessToken, */reviewId, shopId,reviewReportDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);

    }

//    // 리뷰 신고
//    @PostMapping("/review/report/{reviewId}")
//    public String reviewReport(/*@RequestHeader(AUTH) String accessToken,*/
//            @PathVariable Long reviewId,
//            @RequestParam(value = "shopId") Long shopId,
//            @ModelAttribute ReviewReportDto reviewReportDto,
//            RedirectAttributes redirectAttributes){
//        try {
//            CommonResponseDto<Object> commonResponseDto = reviewService.reviewReport(/*accessToken, */reviewId, shopId,reviewReportDto);
//            ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
//
//            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());
//
//            redirectAttributes.addAttribute("reviewId", reviewId);
//            redirectAttributes.addAttribute("shopId", shopId);
//
//            // TODO 리다이렉트 어쩌지
//            return "redirect:owner/review_manage";
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.REVIEW_REPORT_FAIL);
//
//            return "review/review_report";
//        }
//
//    }

    // 리뷰 신고 뷰
    @GetMapping("/review/report/{reviewId}")
    public String reviewReportView (Model model,
                                    /*@RequestHeader(AUTH) String accessToken,*/
                                    @PathVariable Long reviewId,
                                    @RequestParam(value = "shopId") Long shopId){
        CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, shopId);
        ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
        resultDto.setData((ReviewDetailResponseDto) detailReview.getData());

        model.addAttribute("result", resultDto);

        return "review/review_report";
    }

    // 리뷰 삭제
    @DeleteMapping("/mypage/review/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewDelete(/*@RequestHeader(AUTH) String accessToken,*/
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewDelete(/*accessToken, */ reviewId, shopId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }


}
