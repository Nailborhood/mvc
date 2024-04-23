package com.nailshop.nailborhood.service.shop;

import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopAndReviewLookUpResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopReviewListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopReviewListLookupService {
    private final CommonService commonService;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ShopRepository shopRepository;
    private final ShopImgRepository shopImgRepository;
    private final CategoryReviewRepository categoryReviewRepository;

    // 매장 리뷰 조회
    // 카데고리 없이 전체 리뷰 조회
    @Transactional
    public CommonResponseDto<Object> getAllReviewListByShopId(int page, int size, String criteria, String sort, Long shopId) {

        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, criteria)) : PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, criteria));
        // 페이지로 값 가져오기
        Page<Review> reviews = reviewRepository.findAllNotDeletedByShopId(pageable, shopId);


       if (reviews.isEmpty()) {
           return commonService.errorResponse(ErrorCode.REVIEW_NOT_REGISTRATION.getDescription(), HttpStatus.OK, null);
       }



            // review entity -> dto 변환
        Page<ShopAndReviewLookUpResponseDto> data = reviews.map(review -> {
                Long reviewId = review.getReviewId();
                // 리뷰 이미지 가져오기
                String reviewImgPath = reviewImgRepository.findReviewImgByShopIdAndReviewId(shopId, reviewId);
                String shopMainImgPath = shopImgRepository.findShopImgByShopId(shopId);

                // dto에 shop entity 값을 변환하는 과정
                ShopAndReviewLookUpResponseDto dto = ShopAndReviewLookUpResponseDto.builder()
                        .reviewId(reviewId)
                        .contents(review.getContents())
                        .rate(review.getRate())
                        .reviewImgPath(reviewImgPath)
                        .createdAt(review.getCreatedAt())
                        .nickName(review.getCustomer().getMember().getNickname())
                        .shopName(shop.getName())
                        .shopAddress(shop.getAddress())
                        .time(shop.getOpentime())
                        .reviewAvg(shop.getRateAvg())
                        .reviewCnt(shop.getReviewCnt())
                        .favoriteCnt(shop.getFavoriteCnt())
                        .shopMainImgPath(shopMainImgPath)
                        .shopId(shopId)
                        .build();
                // data 에 dto 반환
                return dto;
            });


        // 리뷰 리스트 가져오기
        List<ShopAndReviewLookUpResponseDto> shopReviewLookupResponseDtoList = data.getContent();


        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(data.getTotalPages())
                                                   .totalElements(data.getTotalElements())
                                                   .pageNo(data.getNumber())
                                                   .isLastPage(data.isLast())
                                                   .build();

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopReviewListResponseDto shopReviewListResponseDto = ShopReviewListResponseDto.builder()
                                                                                        .shopAndReviewLookUpResponseDto(shopReviewLookupResponseDtoList)
                                                                                        .paginationDto(paginationDto)
                                                                                        .build();


        return commonService.successResponse(SuccessCode.SHOP_REVIEW_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopReviewListResponseDto);
    }

    // 매장 리뷰 조회
    // 카데고리, 검색 통합 리뷰 조회
    @Transactional
    public CommonResponseDto<Object> getSearchReviewListByShopId(int page, int size, String category, String criteria, String keyword, Long shopId) {

        // category 리스트화
        List<Long> categoryIdList;
        if (category != null && !category.isEmpty()){

            categoryIdList = Arrays.stream(category.split(","))
                    .map(Long::parseLong)
                    .toList();
        } else {
            categoryIdList = null;
        }

        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(criteria).descending());
        // 페이지로 값 가져오기
//        Page<Review> reviews = reviewRepository.findAllNotDeletedByShopId(pageable, shopId);
        Page<Review> reviews;

        if(keyword == null || keyword.trim()
                .isEmpty()) {
            if (categoryIdList == null || categoryIdList.isEmpty()){
                reviews = reviewRepository.findAllNotDeletedByShopId(pageable, shopId);
            } else {
                int categoryIdListSize = categoryIdList.size();
                reviews = reviewRepository.findAllByCategoryIdListNotDeletedByShopId(categoryIdList, categoryIdListSize, pageable, shopId);
            }
        } else {
            if (categoryIdList == null || categoryIdList.isEmpty()){
                reviews = reviewRepository.findShopReviewListBySearch(keyword, pageable, shopId);
            } else {
                int categoryIdListSize = categoryIdList.size();
                reviews = reviewRepository.findShopReviewByKeywordAndCategories(keyword, categoryIdList, categoryIdListSize, pageable, shopId);
            }
        }

//        if (reviews.isEmpty()) {
//            return commonService.errorResponse(ErrorCode.REVIEW_NOT_REGISTRATION.getDescription(), HttpStatus.OK, null);
//        }


        // review entity -> dto 변환
        Page<ShopAndReviewLookUpResponseDto> data = reviews.map(review -> {
            Long reviewId = review.getReviewId();
            // 리뷰 이미지 가져오기
            String reviewImgPath = reviewImgRepository.findReviewImgByShopIdAndReviewId(shopId, reviewId);
            String shopMainImgPath = shopImgRepository.findShopImgByShopId(shopId);
            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            // dto에 shop entity 값을 변환하는 과정
            ShopAndReviewLookUpResponseDto dto = new ShopAndReviewLookUpResponseDto(
                    reviewId,
                    review.getContents(),
                    review.getRate(),
                    reviewImgPath,
                    review.getCreatedAt(),
                    review.getCustomer()
                            .getMember()
                            .getNickname(),
                    shop.getName(),
                    shop.getAddress(),
                    shop.getOpentime(),
                    shop.getRateAvg(),
                    shop.getReviewCnt(),
                    shop.getFavoriteCnt(),
                    shopMainImgPath,
                    shopId,
                    categoryIdList,
                    categoryTypeList

            );
            // data 에 dto 반환
            return dto;
        });


        // 리뷰 리스트 가져오기
        List<ShopAndReviewLookUpResponseDto> shopReviewLookupResponseDtoList = data.getContent();


        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(data.getTotalPages())
                .totalElements(data.getTotalElements())
                .pageNo(data.getNumber())
                .isLastPage(data.isLast())
                .build();

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopReviewListResponseDto shopReviewListResponseDto = ShopReviewListResponseDto.builder()
                .shopAndReviewLookUpResponseDto(shopReviewLookupResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.SHOP_REVIEW_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopReviewListResponseDto);
    }

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
