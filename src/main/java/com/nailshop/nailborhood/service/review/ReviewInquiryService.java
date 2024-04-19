package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewLikeRepository;
import com.nailshop.nailborhood.repository.review.ReviewReportRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
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

import java.util.*;

@RequiredArgsConstructor
@Service
public class ReviewInquiryService {

    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CategoryReviewRepository categoryReviewRepository;


    // 리뷰 상세조회
    public CommonResponseDto<Object> detailReview(Long reviewId, Long shopId, MemberDetails memberDetails) {

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 가져오기
        Review review = reviewRepository.findDetailReview(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 고객 닉네임, 프로필 이미지 가져오기
        String nickName = review.getCustomer().getMember().getNickname();
        String profileImg = review.getCustomer().getMember().getProfileImg();

        Boolean reviewLikeStatus = reviewLikeRepository.findStatusByMemberIdAndReviewId(memberDetails.getMember().getMemberId(), reviewId);
        if(reviewLikeStatus == null){
            reviewLikeStatus = false;
        }



        // 영업상태, 신고상태, 카테고리
        ShopStatus shopStatus = shop.getStatus();

        ReviewReport reviewReport = reviewReportRepository.findReviewReportByReviewId(reviewId);
        String reviewReportStatus = (reviewReport != null) ? reviewReport.getStatus() : "신고 되지 않았음";

        List<String> categoryList = categoryReviewRepository.findCategoryTypeByReviewId(reviewId);



        //리뷰 이미지
        List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
        Map<Integer, String> reviewImgPathMap = new HashMap<>();
        for (ReviewImg reviewImg : reviewImgList) {
            reviewImgPathMap.put(reviewImg.getImgNum(), reviewImg.getImgPath());
        }

        ReviewDetailResponseDto reviewDetailResponseDto = ReviewDetailResponseDto.builder()
                .reviewId(reviewId)
                .shopId(shopId)
                .shopName(shop.getName())
                .shopStatus(shopStatus)
                .shopAddress(shop.getAddress())
                .reviewReportStatus(reviewReportStatus)
                .categoryTypeList(categoryList)
                .imgPathMap(reviewImgPathMap)
                .contents(review.getContents())
                .rate(review.getRate())
                .likeCnt(review.getLikeCnt())
                .reviewAuthor(nickName)
                .reviewAuthorProfileImg(profileImg)
                .reviewCreatedAt(review.getCreatedAt())
                .reviewUpdatedAt(review.getUpdatedAt())
                .reviewLikeStatus(reviewLikeStatus)
                .isDeleted(review.isDeleted())
                .build();

        return commonService.successResponse(SuccessCode.REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, reviewDetailResponseDto);
    }

    public CommonResponseDto<Object> detailReviewForGuest(Long reviewId, Long shopId) {

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 가져오기
        Review review = reviewRepository.findDetailReview(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 고객 닉네임, 프로필 이미지 가져오기
        String nickName = review.getCustomer().getMember().getNickname();
        String profileImg = review.getCustomer().getMember().getProfileImg();


        // 영업상태, 신고상태, 카테고리
        ShopStatus shopStatus = shop.getStatus();

        ReviewReport reviewReport = reviewReportRepository.findReviewReportByReviewId(reviewId);
        String reviewReportStatus = (reviewReport != null) ? reviewReport.getStatus() : "신고 되지 않았음";

        List<String> categoryList = categoryReviewRepository.findCategoryTypeByReviewId(reviewId);



        //리뷰 이미지
        List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
        Map<Integer, String> reviewImgPathMap = new HashMap<>();
        for (ReviewImg reviewImg : reviewImgList) {
            reviewImgPathMap.put(reviewImg.getImgNum(), reviewImg.getImgPath());
        }

        ReviewDetailResponseDto reviewDetailResponseDto = ReviewDetailResponseDto.builder()
                .reviewId(reviewId)
                .shopId(shopId)
                .shopName(shop.getName())
                .shopStatus(shopStatus)
                .shopAddress(shop.getAddress())
                .reviewReportStatus(reviewReportStatus)
                .categoryTypeList(categoryList)
                .imgPathMap(reviewImgPathMap)
                .contents(review.getContents())
                .rate(review.getRate())
                .likeCnt(review.getLikeCnt())
                .reviewAuthor(nickName)
                .reviewAuthorProfileImg(profileImg)
                .reviewCreatedAt(review.getCreatedAt())
                .reviewUpdatedAt(review.getUpdatedAt())
//                .reviewLikeStatus(reviewLikeStatus)
                .isDeleted(review.isDeleted())
                .build();

        return commonService.successResponse(SuccessCode.REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, reviewDetailResponseDto);
    }


    // 리뷰 전체 조회
    public CommonResponseDto<Object> allReview(String keyword, int page, int size, String criteria, String category) {

        // category 리스트화
        List<Long> categoryIdList = null;
        if (category != null && !category.isEmpty()){

            categoryIdList = Arrays.stream(category.split(","))
                    .map(Long::parseLong)
                    .toList();
        }

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(criteria).descending());
        Page<Review> reviewPage;
        List<Review> reviewList;

        if(keyword == null || keyword.trim()
                                     .isEmpty()) {
            if (categoryIdList == null || categoryIdList.isEmpty()){
                reviewPage = reviewRepository.findAllIsDeletedFalse(pageable);
            } else {
                int categoryIdListSize = categoryIdList.size();
                reviewPage = reviewRepository.findByCategoryIdListAndIsDeletedFalse(categoryIdList, categoryIdListSize, pageable);
            }
        } else {
            if (categoryIdList == null || categoryIdList.isEmpty()){
                reviewPage = reviewRepository.findReviewListBySearch(keyword, pageable);
            } else {
                int categoryIdListSize = categoryIdList.size();
                reviewPage = reviewRepository.findReviewByKeywordAndCategories(keyword, categoryIdList, categoryIdListSize, pageable);
            }
        }

//        if (reviewPage.isEmpty()) throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);

        reviewList = reviewPage.getContent();
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Review review : reviewList ){

            String mainImgPath = review.getReviewImgList().getFirst().getImgPath();
            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .shopId(review.getShop().getShopId())
                    .mainImgPath(mainImgPath)
                    .categoryIdList(categoryIdList)
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

    // 조회 정렬 기준 orderby 설정
    public List<Map<String, String>> createCriteriaOptions() {
        List<Map<String, String>> sortOptions = new ArrayList<>();

        Map<String, String> option1 = new HashMap<>();
        option1.put("value", "likeCnt");
        option1.put("text", "인기순");

        sortOptions.add(option1);

        Map<String, String> option2 = new HashMap<>();
        option2.put("value", "createdAt");
        option2.put("text", "최신순");
        sortOptions.add(option2);

        return sortOptions;
    }

}
