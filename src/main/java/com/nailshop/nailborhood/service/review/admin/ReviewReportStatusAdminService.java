package com.nailshop.nailborhood.service.review.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportLookupDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewLikeRepository;
import com.nailshop.nailborhood.repository.review.ReviewReportRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
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
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewRepository reviewRepository;
    private final S3UploadService s3UploadService;
    private final ShopRepository shopRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CategoryReviewRepository categoryReviewRepository;


    // 리뷰 신고 조회
    public CommonResponseDto<Object> getReviewReports(String keyword, int page, int size, String sort) {

        // 관리자 확인
//        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
//                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
//        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort)
                                                               .descending());
        Page<ReviewReport> reviewReportPage;

        if (keyword == null || keyword.trim()
                                      .isEmpty()) {

            reviewReportPage = reviewReportRepository.findAllNotDeleted(pageable, "신고 대기중");
        } else {
            reviewReportPage = reviewReportRepository.findAllReviewReportListBySearch(keyword, pageable, "신고 대기중");
        }

        if (reviewReportPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.REVIEW_REPORT_NOT_FOUND);
        }

        // ReviewReport entity -> dto 변환
        Page<ReviewReportLookupDto> data = reviewReportPage.map(reviewReport -> {

            String mainImgPath = reviewImgRepository.findByReviewImgListReviewId(reviewReport.getReview()
                                                                                             .getReviewId())
                                                    .getFirst()
                                                    .getImgPath();
            ReviewReportLookupDto dto = new ReviewReportLookupDto(
                    reviewReport.getReportId(),
                    mainImgPath,
                    reviewReport.getContents(),
                    reviewReport.getDate(),
                    reviewReport.getStatus(),
                    reviewReport.getReview()
                                .getReviewId(),
                    reviewReport.getReview()
                                .getCustomer()
                                .getMember()
                                .getNickname(),
                    reviewReport.getReview()
                                .getContents(),
                    reviewReport.getMember()
                                .getNickname(),
                    reviewReport.getReview()
                                .getShop()
                                .getName()

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


    // 신고된 리뷰 처리된 상태 조회
    public CommonResponseDto<Object> getReviewReportStatus(String keyword, int page, int size, String sort) {

        // 관리자 확인
//        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
//                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
//        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort)
                                                               .descending());
        Page<ReviewReport> reviewReportPage;

        if (keyword == null || keyword.trim()
                                      .isEmpty()) {

            // 신고 처리된 리스트 -> 리뷰 isDeleted= true && 리뷰 신고 처리 상태: 신고처리됨
            // 신고 반려된 리스트 -> 리뷰 isDeleted= false && 리뷰 신고 처리 상태: 신고반려됨
            reviewReportPage = reviewReportRepository.findAllByStatus(pageable, "신고 처리됨", "신고 반려됨");
        } else {
            reviewReportPage = reviewReportRepository.findAllReviewReportListBySearchAndStatus(keyword, pageable, "신고 처리됨", "신고 반려됨");
        }

        if (reviewReportPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.REVIEW_REPORT_NOT_FOUND);
        }

        // ReviewReport entity -> dto 변환
        Page<ReviewReportLookupDto> data = reviewReportPage.map(reviewReport -> {

            String mainImgPath = reviewImgRepository.findByReviewImgListReviewId(reviewReport.getReview()
                                                                                             .getReviewId())
                                                    .getFirst()
                                                    .getImgPath();
            ReviewReportLookupDto dto = new ReviewReportLookupDto(
                    reviewReport.getReportId(),
                    mainImgPath,
                    reviewReport.getContents(),
                    reviewReport.getDate(),
                    reviewReport.getStatus(),
                    reviewReport.getReview()
                                .getReviewId(),
                    reviewReport.getReview()
                                .getCustomer()
                                .getMember()
                                .getNickname(),
                    reviewReport.getReview()
                                .getContents(),
                    reviewReport.getMember()
                                .getNickname(),
                    reviewReport.getReview()
                                .getShop()
                                .getName()

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
    public CommonResponseDto<Object> changeReviewStatus(Long reportId, String status) {

        // 관리자 확인
       /* Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
*/
        String reviewStatus = null;
        SuccessCode successCode = null;

        if (status.equals("reject")) {
            // 리뷰 신고 반려 -> 리뷰 남는다 / 신고 테이블 반려됨으로 표시
            reviewStatus = ReviewReportStatus.REVIEW_REPORT_REJECTED.getDescription();
            successCode = SuccessCode.REVIEW_REPORT_STATUS_REJECT_SUCCESS;


            reviewReportRepository.updateReviewStatusByReviewId(reportId, reviewStatus);


        } else if (status.equals("accept")) {
            // 리뷰 신고 승인 -> 리뷰 삭제 / 신고 테이블 승인 표시
            reviewStatus = ReviewReportStatus.REVIEW_REPORT_ACCEPTED.getDescription();
            successCode = SuccessCode.REVIEW_REPORT_STATUS_ACCEPT_SUCCESS;


            // reportId에 해당되는 reviewId 가져오기
            Long reviewId = reviewReportRepository.findReviewIdByReportId(reportId);

            // 신고 상태 신고 승인됨으로 변경
            List<ReviewReport> reportList = reviewReportRepository.findReviewReportListByReviewId(reviewId);

            for (ReviewReport report : reportList) {

                reviewReportRepository.updateReviewStatusByReviewId(report.getReportId(), reviewStatus);
            }


            // 리뷰 이미지 삭제

            List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
            for (ReviewImg reviewImg : reviewImgList) {
                String reviewImgUrl = reviewImg.getImgPath();
                s3UploadService.deleteReviewImg(reviewImgUrl);
                reviewImgRepository.deleteByReviewImgId(reviewImg.getReviewImgId(), true);
            }


            // 삭제 되는 리뷰 좋아요 수 0, reviewLike 삭제
            reviewRepository.likeCntZero(reviewId);
            reviewLikeRepository.deleteByReviewId(reviewId);

            // reviewID 해당 카테고리 삭제
            categoryReviewRepository.deleteByReviewReviewId(reviewId);

            // 매장 리뷰 개수 감소
            Review review = reviewRepository.findByReviewId(reviewId);
            shopRepository.updateReviewCntDecreaseByShopId(review.getShop()
                                                                 .getShopId());


            // 리뷰 삭제
            reviewRepository.deleteReviewId(reviewId, true);

            // 리뷰 별점 삭제, 매장 리뷰 평균 별점 수정
            updateShopRateAvg(review.getShop());
        }

        if (reviewStatus == null) {
            throw new BadRequestException(ErrorCode.REVIEW_REPORT_INCORRECT);

        }


        return commonService.successResponse(successCode.getDescription(), HttpStatus.OK, null);
    }

    // 리뷰 평균 별점 수정
    private void updateShopRateAvg(Shop shop) {
        Long shopId = shop.getShopId();
        List<Review> reviews = reviewRepository.findAllByShopIdAndIsDeleted(shopId);

        double totalRate = reviews.stream()
                                  .mapToInt(Review::getRate)
                                  .sum();
        if (totalRate != 0) {
            String rateAvgStr = String.format("%.1f", totalRate / reviews.size());
            double rateAvg = Double.parseDouble(rateAvgStr);
            shopRepository.updateRateAvgByShopId(rateAvg, shopId);
        } else {
            shopRepository.updateRateAvgByShopId(0, shopId);
        }

    }

    // 리뷰 신고 상세 조회
    public CommonResponseDto<Object> getReviewReportDetail(Long reportId) {
        ReviewReport report = reviewReportRepository.findByReportIdAndStatus(reportId, "신고 대기중");

        if (report == null) {
            throw new NotFoundException(ErrorCode.REVIEW_REPORT_NOT_FOUND);
        }

        List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(report.getReview()
                                                                                              .getReviewId());


        ReviewReportLookupDto reviewReportLookupDto = ReviewReportLookupDto.builder()
                                                                           .reportId(report.getReportId())
                                                                           .reporter(report.getMember()
                                                                                           .getNickname())
                                                                           .reviewer(report.getReview()
                                                                                           .getCustomer()
                                                                                           .getMember()
                                                                                           .getNickname())
                                                                           .reportContents(report.getContents())
                                                                           .reviewContents(report.getReview()
                                                                                                 .getContents())
                                                                           .date(report.getDate())
                                                                           .reviewId(report.getReview()
                                                                                           .getReviewId())
                                                                           .status(report.getStatus())
                                                                           .mainImgPath(reviewImgList.getFirst()
                                                                                                     .getImgPath())
                                                                           .shopName(report.getReview()
                                                                                           .getShop()
                                                                                           .getName())
                                                                           .build();

        return commonService.successResponse(SuccessCode.All_REVIEW_REPORT_SUCCESS.getDescription(), HttpStatus.OK, reviewReportLookupDto);
    }
}
