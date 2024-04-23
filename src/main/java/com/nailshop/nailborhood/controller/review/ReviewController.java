package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.review.request.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.request.ReviewUpdateDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.review.ReviewInquiryService;
import com.nailshop.nailborhood.service.review.ReviewService;
import com.nailshop.nailborhood.dto.review.request.ReviewRegistrationRequestDto;
import com.nailshop.nailborhood.service.review.ReviewRegistrationService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.AlarmType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewInquiryService reviewInquiryService;
    private final ReviewRegistrationService reviewRegistrationService;
    private final MemberService memberService;
    private final ShopDetailService shopDetailService;
    private final CategoryRepository categoryRepository;

    // 리뷰 등록(GET)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_OWNER')")
    @GetMapping("/{shopId}/review/registration")
    public String showRegisterReview(Authentication authentication,
                                     @AuthenticationPrincipal MemberDetails memberDetails,
                                     Model model,
                                     @PathVariable Long shopId) {

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }

        List<Category> categoryList = categoryRepository.findAll();

        model.addAttribute("shopId", shopId);
        model.addAttribute("categories", categoryList);

        return "review/review_registration";
    }

    // 리뷰 등록(POST)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_USER')")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/{shopId}/review/registration")
    public String registerReview(@PathVariable Long shopId,
                                 Authentication authentication,
                                 Model model,
                                 @AuthenticationPrincipal MemberDetails memberDetails,
                                 @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                 @ModelAttribute ReviewRegistrationRequestDto reviewRegistrationRequestDto,
                                 RedirectAttributes redirectAttributes) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        try {
            Long reviewId = reviewRegistrationService.saveReview(shopId, sessionDto.getId(), multipartFileList, reviewRegistrationRequestDto);
//            return "redirect:/review/inquiry/" + reviewId+"?shopId="+shopId;
            return String.format("redirect:/review/inquiry/%d?shopId=%d&alarmSent=true", reviewId, shopId);
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.REVIEW_REGISTER_FAIL);

            return "review/review_registration";
        }
    }

    // 리뷰 수정
    @PostMapping(consumes = {"multipart/form-data"}, value = "/review/update/{reviewId}")
    public String reviewUpdate(Authentication authentication,
                               @AuthenticationPrincipal MemberDetails memberDetails,
                               @PathVariable Long reviewId,
                               @RequestParam(value = "shopId") Long shopId,
                               @RequestPart(value = "img") List<MultipartFile> multipartFileList,
                               @ModelAttribute ReviewUpdateDto reviewUpdateDto,
                               RedirectAttributes redirectAttributes) {
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        try {
            CommonResponseDto<Object> commonResponseDto = reviewService.reviewUpdate(sessionDto.getId(), reviewId, shopId, multipartFileList, reviewUpdateDto);
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
    public String getReviewUpdate(Authentication authentication,
                                  @AuthenticationPrincipal MemberDetails memberDetails,
                                  Model model,
                                  @PathVariable Long reviewId,
                                  @RequestParam(value = "shopId") Long shopId) {

        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, shopId, sessionDto.getId());
        ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
        resultDto.setData((ReviewDetailResponseDto) detailReview.getData());

        model.addAttribute("result", resultDto);

        return "review/review_mod";
    }

    // 리뷰 신고
    @PostMapping("/review/report/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewReport(Authentication authentication,
                                                        @AuthenticationPrincipal MemberDetails memberDetails,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId,
                                                        @RequestBody ReviewReportDto reviewReportDto) {
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);

        CommonResponseDto<Object> commonResponseDto = reviewService.reviewReport(sessionDto.getId(), reviewId, shopId, reviewReportDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);

    }


    // 리뷰 신고 뷰
    @GetMapping("/review/report/{reviewId}")
    public String reviewReportView(Model model,
                                   @AuthenticationPrincipal MemberDetails memberDetails,
                                   Authentication authentication,
                                   @PathVariable Long reviewId,
                                   @RequestParam(value = "shopId") Long shopId) {

        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, shopId, sessionDto.getId());
        ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
        resultDto.setData((ReviewDetailResponseDto) detailReview.getData());

        model.addAttribute("result", resultDto);

        return "review/review_report";
    }


    // 리뷰 삭제
    @DeleteMapping("/mypage/review/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewDelete(Authentication authentication,
                                                        @AuthenticationPrincipal MemberDetails memberDetails,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId) {

        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);

        CommonResponseDto<Object> commonResponseDto = reviewService.reviewDelete(sessionDto.getId(), reviewId, shopId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }

//    // enum 타임리프로 리턴
//    @ModelAttribute("alarmType")
//    public AlarmType[] alarmType() {
//        return AlarmType.values();
//    }

}
