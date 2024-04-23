package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.category.CategoryReview;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.review.request.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.request.ReviewUpdateDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewLikeRepository;
import com.nailshop.nailborhood.repository.review.ReviewReportRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.SuccessCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryReviewRepository categoryReviewRepository;
    private final CommonService commonService;
    private final S3UploadService s3UploadService;

    // 리뷰 수정
    @Transactional
    public CommonResponseDto<Object> reviewUpdate(Long memberId, Long reviewId, Long shopId, List<MultipartFile> multipartFileList, ReviewUpdateDto reviewUpdateDto) {

        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 가져오기
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자와 수정하려는 유저 동일 검증
        if (!review.getCustomer().getMember().getNickname().equals(member.getNickname())) {
            throw new BadRequestException(ErrorCode.AUTHOR_NOT_EQUAL);
        }

        // 리뷰 정보 저장
        review.reviewUpdate(reviewUpdateDto.getReviewId() ,reviewUpdateDto.getContents(), reviewUpdateDto.getRate());
        reviewRepository.save(review);


        // 기존 이미지 삭제
        removeExistReviewImg(multipartFileList, review);

        // 새로운 이미지 저장
        saveReviewImg(multipartFileList, review);

        // 리뷰 평균 별점 수정
        updateShopRateAvg(shop);

        // 리뷰 카테고리 수정
        //TODO
//        categoryReviewRepository.deleteByReviewReviewId(reviewId);

//        for(Long categoryId : reviewUpdateDto.getCategoryListId()){
//            Category category = categoryRepository.findById(categoryId)
//                    .orElseThrow(() -> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
//
//            CategoryReview categoryReview = CategoryReview.builder()
//                    .review(review)
//                    .category(category)
//                    .build();
//            categoryReviewRepository.save(categoryReview);
//        }


        return commonService.successResponse(SuccessCode.REVIEW_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    // 리뷰 신고
    @Transactional
    public CommonResponseDto<Object> reviewReport(Long memberId, Long reviewId, Long shopId, ReviewReportDto reviewReportDto) {

        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 매장 존재 여부
        shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 존재 여부
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 신고당한 유저 ID를 가져올까 닉네임을 가져올까
//        Long reportedCustomerId = review.getCustomer().getCustomerId();


        ReviewReport reviewReport = ReviewReport.builder()
                .contents(reviewReportDto.getReportReason())
                .status(ReviewReportStatus.REVIEW_REPORT_PENDING.getDescription())
                .member(member)
                .review(review)
                .date(LocalDateTime.now())
                .build();

        reviewReportRepository.save(reviewReport);

        return commonService.successResponse(SuccessCode.REVIEW_REPORT_SUCCESS.getDescription(), HttpStatus.OK, null);
    }



    // 리뷰 삭제
    @Transactional
    public CommonResponseDto<Object> reviewDelete(Long memberId, Long reviewId, Long shopId) {

        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 존재여부
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자와 삭제하려는 유저 동일 검증 추가
        if (!review.getCustomer().getMember().getNickname().equals(member.getNickname())) {
            throw new BadRequestException(ErrorCode.AUTHOR_NOT_EQUAL);
        }


        // 이미지 삭제 true -> false
        List<ReviewImg> reviewImgPathList = reviewImgRepository.findDeleteReviewImgPathList(reviewId);

        // url 삭제
        for(ReviewImg reviewImgPath : reviewImgPathList) {
            String imgPath = reviewImgPath.getImgPath();

            s3UploadService.deleteReviewImg(imgPath);

            reviewImgRepository.deleteByReviewImgId(reviewImgPath.getReviewImgId(),true);
        }

        // 좋아요 수 0, reviewLike status false로 변경
        reviewRepository.likeCntZero(reviewId);
        reviewLikeRepository.deleteByReviewId(reviewId, false);

        // 매장 리뷰 cnt 감소 처리
        shopRepository.updateReviewCntDecreaseByShopId(shopId);

        // reviewID 해당 카테고리 삭제
        categoryReviewRepository.deleteByReviewReviewId(reviewId);

        // 리뷰 신고 테이블 reviewId 해당 컬럼 삭제
/*        reviewReportRepository.deleteReviewReportByReviewId(reviewId);*/

        // 리뷰 isdeleted 값 true로
        reviewRepository.deleteReviewId(reviewId, true);

        updateShopRateAvg(shop);

        return commonService.successResponse(SuccessCode.REVIEW_DELETE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }


    // 리뷰 평균 별점 수정
    private void updateShopRateAvg(Shop shop) {
        Long shopId = shop.getShopId();
        List<Review> reviews = reviewRepository.findAllByShopIdAndIsDeleted(shopId);

        double totalRate = reviews.stream()
                                  .mapToInt(Review::getRate)
                                  .sum();
        if(totalRate != 0 ) {
            String rateAvgStr = String.format("%.1f", totalRate / reviews.size());
            double rateAvg = Double.parseDouble(rateAvgStr);
            shopRepository.updateRateAvgByShopId(rateAvg,shopId);
        }else {
            shopRepository.updateRateAvgByShopId(0,shopId);
        }

    }



    // 기존 리뷰 이미지 삭제
    private void removeExistReviewImg(List<MultipartFile> multipartFileList, Review review) {
        Long reviewId = review.getReviewId();

        if(multipartFileList != null) {
            List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
            for(ReviewImg reviewImg : reviewImgList) {
                String reviewImgUrl = reviewImg.getImgPath();
                s3UploadService.deleteReviewImg(reviewImgUrl);
            }

            reviewImgRepository.deleteByReviewId(reviewId);
        }
    }

    // 이미지 저장
    private void saveReviewImg(List<MultipartFile> multipartFileList, Review review) {
        // s3에 이미지 업로드
        List<String> reviewImgUrlList = s3UploadService.reviewImgUpload(multipartFileList);

        // 이미지 번호 1번 부터 시작
        Integer imgNum = 1;

        for (String imgPath : reviewImgUrlList) {
            ReviewImg reviewImg = ReviewImg.builder()
                    .imgPath(imgPath)
                    .imgNum(imgNum)
                    .isDeleted(false)
                    .review(review)
                    .build();
            reviewImgRepository.save(reviewImg);

            imgNum++;
        }
    }


}
