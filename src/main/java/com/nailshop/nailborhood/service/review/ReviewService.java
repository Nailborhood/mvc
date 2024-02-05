package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.review.ReviewUpdateDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ShopRepository shopRepository;
    private final CommonService commonService;
    private final S3UploadService s3UploadService;

    // 리뷰 수정
    @Transactional
    public CommonResponseDto<Object> reviewUpdate(Long reviewId, Long shopId, List<MultipartFile> multipartFileList, ReviewUpdateDto reviewUpdateDto) {

        // 매장 정보 확인
        shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 가져오기
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰 정보 저장
        review.reviewUpdate(reviewUpdateDto.getContents(), reviewUpdateDto.getRate());
        reviewRepository.save(review);

        // 기존 이미지 삭제
        removeExistReviewImg(multipartFileList, review);

        // 새로운 이미지 저장
        saveReviewImg(multipartFileList, review);



        return commonService.successResponse(SuccessCode.REVIEW_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    // 리뷰 신고
//    @Transactional
//    public CommonResponseDto<Object> reviewReport(Long reviewId, ReviewReportDto reviewReportDto) {
//
//        // 유저 정보 확인
//
//        // 리뷰 가져오기
//        Review review = reviewRepository.findReviewByFalse(reviewId)
//                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
//
//        String reportReason = reviewReportDto.getReportReason();
//        reviewRepository.
//
//
//
//        return commonService.successResponse(SuccessCode.EXAMPLE_SUCCESS.getDescription(), HttpStatus.OK, null);
//    }



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
