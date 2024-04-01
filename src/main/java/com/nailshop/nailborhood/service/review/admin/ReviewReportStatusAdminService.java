package com.nailshop.nailborhood.service.review.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportLookupDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.review.ReviewReportRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewReportStatusAdminService {
    private final ReviewReportRepository reviewReportRepository;
    private final CommonService commonService;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;


    // 리뷰 신고 조회
    public CommonResponseDto<Object> getReviewReports(String accessToken, int page, int size, String sort) {

        // 관리자 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ROLE_ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort)
                                                               .descending());
        Page<ReviewReport> reviewReports = reviewReportRepository.findAllNotDeleted(pageable);

        if (reviewReports.isEmpty()) {
            throw new NotFoundException(ErrorCode.REVIEW_REPORT_NOT_FOUND);
        }

        // ReviewReport entity -> dto 변환
        Page<ReviewReportLookupDto> data = reviewReports.map(reviewReport -> {

            ReviewReportLookupDto dto = new ReviewReportLookupDto(
                    reviewReport.getReportId(),
                    reviewReport.getContents(),
                    reviewReport.getDate(),
                    reviewReport.getStatus(),
                    reviewReport.getReview()
                                .getReviewId(),
                    reviewReport.getReview()
                                .getContents(),
                    reviewReport.getMember().getNickname()

            );
            return dto;
        });

        // 리뷰 리스트 가져오기
        List<ReviewReportLookupDto> reviewReportLookupDtoList = data.getContent();

        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(data.getTotalPages())
                                                   .totalElements(data.getTotalElements())
                                                   .pageNo(data.getNumber())
                                                   .isLastPage(data.isLast())
                                                   .build();

        // 페이지네이션을 포함한 리뷰 리스트 반환
        ReviewReportListLookupDto reviewReportListLookupDto = ReviewReportListLookupDto.builder()
                                                                                       .reviewReportLookupDtoList(reviewReportLookupDtoList)
                                                                                       .paginationDto(paginationDto)
                                                                                       .build();

        return commonService.successResponse(SuccessCode.All_REVIEW_REPORT_SUCCESS.getDescription(), HttpStatus.OK, reviewReportListLookupDto);
    }

    @Transactional
    // 리뷰 신고 처리
    public CommonResponseDto<Object> changeReviewStatus(String accessToken, Long reportId, String status) {

        // 관리자 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ROLE_ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        String reviewStatus = null;
        SuccessCode successCode = null;


        if (status.equals("reject")) {
            reviewStatus = ReviewReportStatus.REVIEW_REPORT_REJECTED.getDescription();
            successCode = SuccessCode.REVIEW_REPORT_STATUS_REJECT_SUCCESS;
        } else if (status.equals("accept")) {
            reviewStatus = ReviewReportStatus.REVIEW_REPORT_ACCEPTED.getDescription();
            successCode = SuccessCode.REVIEW_REPORT_STATUS_ACCEPT_SUCCESS;
        }

        if(reviewStatus == null ) {
            throw new BadRequestException(ErrorCode.REVIEW_REPORT_INCORRECT);
        }
        reviewReportRepository.updateReviewStatusByReviewId(reportId, reviewStatus);

        return commonService.successResponse(successCode.getDescription(), HttpStatus.OK, null);
    }
}
