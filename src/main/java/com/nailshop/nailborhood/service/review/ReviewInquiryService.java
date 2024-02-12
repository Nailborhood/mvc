package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.member.CustomerRepositoryKe;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final CategoryReviewRepository categoryReviewRepository;


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

        // 매장 이름, 영업상태, 신고상태, 카테고리
        // TODO : 리뷰 신고 상태 추가해야함
        ShopStatus shopStatus = shop.getStatus();
        List<String> categoryList = categoryReviewRepository.findCategoryTypeByReviewId(reviewId);

        //리뷰 이미지
        List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
        Map<Integer, String> reviewImgPathMap = new HashMap<>();
        for (ReviewImg reviewImg : reviewImgList) {
            reviewImgPathMap.put(reviewImg.getImgNum(), reviewImg.getImgPath());
        }

        ReviewDetailResponseDto reviewDetailResponseDto = ReviewDetailResponseDto.builder()
                .reviewId(reviewId)
                .shopName(shop.getName())
                .shopStatus(shopStatus)
                .categoryTypeList(categoryList)
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
    public CommonResponseDto<Object> allReview(int page, int size, String sortBy) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortBy).descending());
        Page<Review> reviewPage = reviewRepository.findBAllIsDeletedFalse(pageRequest);

        if(reviewPage.isEmpty()){
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }

        List<Review> reviewList = reviewPage.getContent();
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Review review : reviewList ){

            String mainImgPath = review.getReviewImgList().get(0).getImgPath();

            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .mainImgPath(mainImgPath)
                    .categoryTypeList(categoryTypeList)
                    .contents(review.getContents())
                    .rate(review.getRate())
                    .likeCnt(review.getLikeCnt())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();

            reviewResponseDtoList.add(reviewResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .pageNo(reviewPage.getNumber())
                .isLastPage(reviewPage.isLast())
                .build();

        ReviewListResponseDto reviewListResponseDto = ReviewListResponseDto.builder()
                .reviewResponseDtoList(reviewResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, reviewListResponseDto);
    }

}
