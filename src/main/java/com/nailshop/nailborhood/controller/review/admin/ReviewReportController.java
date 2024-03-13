package com.nailshop.nailborhood.controller.review.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportLookupDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.review.admin.ReviewReportStatusAdminService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RequiredArgsConstructor
@Controller
@RequestMapping("/nailborhood")
public class ReviewReportController {
    private final ReviewReportStatusAdminService reviewReportStatusAdminService;

    // 리뷰 신고 검색

    @GetMapping("/admin/search/reviewReport")
    public String searchReviewReportInquiry(Model model,
                                            //@RequestHeader(AUTH) String accessToken,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                            @RequestParam(value = "sort", defaultValue = "date", required = false) String sort) {
        //TODO: auth 추가되면 변경
        //CommonResponseDto<Object> commonResponseDto = adminSearchService.searchReviewInquiry(accessToken, keyword, page, size, sortBy);
        try {
            CommonResponseDto<Object> allReviewReportList = reviewReportStatusAdminService.getReviewReports(keyword, page, size, sort);
            model.addAttribute("reviewReportList", allReviewReportList.getData());
            return "admin/admin_reviewReport_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.REVIEW_REPORT_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_reviewReport_list";
        }
    }


    @GetMapping("/admin/search/reviewReport/status")
    public String searchReviewReportStatus(Model model,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                            @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                            @RequestParam(value = "sort", defaultValue = "date", required = false) String sort) {

        try {
            CommonResponseDto<Object> allReportStatusList = reviewReportStatusAdminService.getReviewReportStatus(keyword, page, size, sort);
            model.addAttribute("reportStatusList", allReportStatusList.getData());
            return "admin/admin_reviewReportStatus_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.REVIEW_REPORT_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_reviewReportStatus_list";
        }
    }


    // 리뷰 신고 처리 변경
    @PostMapping("/admin/search/reviewReport")
    public String changeReviewReportStatus(@RequestParam Long reportId,
                                           @RequestParam(value = "status") String status) {
        System.out.printf("reportId"+reportId);
        System.out.printf("status"+status);
//        CommonResponseDto<Object> commonResponseDto = reviewReportStatusAdminService.changeReviewStatus(reportId, status);
          reviewReportStatusAdminService.changeReviewStatus(reportId, status);
//        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return "redirect:/nailborhood/admin/search/reviewReport";
    }
}
