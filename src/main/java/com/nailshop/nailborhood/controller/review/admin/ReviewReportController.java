package com.nailshop.nailborhood.controller.review.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportLookupDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.service.review.admin.ReviewReportStatusAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nailborhood")
public class ReviewReportController {
    private final ReviewReportStatusAdminService reviewReportStatusAdminService;

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "리뷰 신고 전체 조회", description = "admin API")
    // 리뷰 신고 전체 조회
    @GetMapping(value = "/admin/reviewReport")
    public ResponseEntity<ResultDto<ReviewReportListLookupDto>> getAllShops(@RequestHeader(AUTH) String accessToken,
                                                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                            @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> reviewReports = reviewReportStatusAdminService.getReviewReports(accessToken,page, size, sort);
        ResultDto<ReviewReportListLookupDto> resultDto = ResultDto.in(reviewReports.getStatus(), reviewReports.getMessage());
        resultDto.setData((ReviewReportListLookupDto) reviewReports.getData());

        return ResponseEntity.status(reviewReports.getHttpStatus())
                             .body(resultDto);
    }


    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "리뷰 신고 처리 변경 ", description = "admin API")
    // 리뷰 신고 처리 변경
    @PutMapping("/admin/reviewReport/{reportId}")
    public ResponseEntity<ResultDto<Void>> changeReviewReportStatus(@RequestHeader(AUTH) String accessToken,
                                                                    @PathVariable Long reportId,
                                                                    @RequestParam(value = "status") String status) {
        CommonResponseDto<Object> commonResponseDto = reviewReportStatusAdminService.changeReviewStatus(accessToken,reportId, status);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }
}
