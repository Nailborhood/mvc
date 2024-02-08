package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.CustomerRepositoryKe;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ReviewInquiryService {

    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final CustomerRepositoryKe customerRepositoryKe;


    // 리뷰 상세조회
    public CommonResponseDto<Object> detailReview(Long reviewId, Long customerId, Long shopId) {

        // 고객 정보와 존재 여부
        Customer customer = customerRepositoryKe.findById(customerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 가져오기
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 고객 닉네임, 프로필 이미지 가져오기
        String nickName = customer.getMember().getNickname();
        String profileImg = customer.getMember().getProfileImg();

        List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
        Map<Integer, String> reviewImgPathMap = new HashMap<>();
        for (ReviewImg reviewImg : reviewImgList) {
            reviewImgPathMap.put(reviewImg.getImgNum(), reviewImg.getImgPath());
        }

        ReviewDetailResponseDto reviewDetailResponseDto = ReviewDetailResponseDto.builder()
                .reviewId(reviewId)
                .imgPathMap(reviewImgPathMap)
                .contents(review.getContents())
                .rate(review.getRate())
                .likeCnt(review.getLikeCnt())
                .reviewAuthor(nickName)
                .reviewAuthorProfileImg(profileImg)
                .reviewCreatedAt(review.getCreatedAt())
                .reviewUpdatedAt(review.getUpdatedAt())
                .build();

        return commonService.successResponse(SuccessCode.REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, reviewDetailResponseDto);
    }


    // 리뷰 조회
//    public CommonResponseDto<Object> allReview(int page, int size, String sortBy) {
//
//
//
//        ExampleDto exampleDto = new ExampleDto("반환 예시 입니다.");
//
//        return commonService.successResponse(SuccessCode.EXAMPLE_SUCCESS.getDescription(), HttpStatus.OK, exampleDto);
//    }

}
