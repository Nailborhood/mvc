package com.nailshop.nailborhood.controller.review.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportLookupDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.review.admin.ReviewReportStatusAdminService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RequiredArgsConstructor
@Controller
public class ReviewReportController {
    private final ReviewReportStatusAdminService reviewReportStatusAdminService;
    private final MemberService memberService;

    // 리뷰 신고 검색
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/search/review/report")
    public String searchReviewReportInquiry(Model model,
                                            @AuthenticationPrincipal MemberDetails memberDetails,
                                            Authentication authentication,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String sortBy) {
        //TODO: auth 추가되면 변경
        //CommonResponseDto<Object> commonResponseDto = adminSearchService.searchReviewInquiry(accessToken, keyword, page, size, sortBy);


        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        try {

            CommonResponseDto<Object> allReviewReportList = reviewReportStatusAdminService.getReviewReports(keyword, page, size, sortBy);
            ResultDto<ReviewReportListLookupDto> resultDto = ResultDto.in(allReviewReportList.getStatus(), allReviewReportList.getMessage());
            resultDto.setData((ReviewReportListLookupDto) allReviewReportList.getData());
            model.addAttribute("resultDto", resultDto);
            return "admin/admin_reviewReport_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.REVIEW_REPORT_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_reviewReport_list";
        }
    }

    // 처리된 리뷰 신고 리스트
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/search/review/report/status")
    public String searchReviewReportStatus(Model model,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                           @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                           @RequestParam(value = "sortBy", defaultValue = "date", required = false) String sortBy,
                                           Authentication authentication,
                                           @AuthenticationPrincipal MemberDetails memberDetails) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        try {
            CommonResponseDto<Object> allReportStatusList = reviewReportStatusAdminService.getReviewReportStatus(keyword, page, size, sortBy);
            ResultDto<ReviewReportListLookupDto> resultDto = ResultDto.in(allReportStatusList.getStatus(), allReportStatusList.getMessage());
            resultDto.setData((ReviewReportListLookupDto) allReportStatusList.getData());
            model.addAttribute("resultDto", resultDto);
            return "admin/admin_reviewReportStatus_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.REVIEW_REPORT_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_reviewReportStatus_list";
        }
    }


    // 리뷰 신고 처리 변경
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/search/review/report")
    public String changeReviewReportStatus(@RequestParam Long reportId,
                                           @RequestParam(value = "status") String status) {
        System.out.printf("reportId" + reportId);
        System.out.printf("status" + status);
//        CommonResponseDto<Object> commonResponseDto = reviewReportStatusAdminService.changeReviewStatus(reportId, status);
        reviewReportStatusAdminService.changeReviewStatus(reportId, status);
//        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return "redirect:/admin/search/review/report/status";
    }


    // 리뷰 신고 상세 조회
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/search/report/detail")
    public String getReviewReportDetail(@RequestParam Long reportId, Model model,
                                        Authentication authentication,
                                        @AuthenticationPrincipal MemberDetails memberDetails) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        try {
            CommonResponseDto<Object> reportDetail = reviewReportStatusAdminService.getReviewReportDetail(reportId);
            model.addAttribute("reportDetail", reportDetail.getData());
            return "admin/admin_reviewReport_detail";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.REVIEW_REPORT_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_reviewReport_detail";
        }


    }
}
