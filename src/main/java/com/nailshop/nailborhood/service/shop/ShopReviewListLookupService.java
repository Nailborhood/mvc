package com.nailshop.nailborhood.service.shop;

import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewLookupResponseDto;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
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
public class ShopReviewListLookupService {
    private final CommonService commonService;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;

    // 매장 리뷰 조회
    // 카데고리 없이 전체 리뷰 조회
    @Transactional
    public CommonResponseDto<Object> getAllReviewListByShopId(int page, int size, String criteria, String sort, Long shopId) {


        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, criteria)) : PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, criteria));
        // 페이지로 값 가져오기
        Page<Review> reviews = reviewRepository.findAllNotDeletedBYShopId(pageable, shopId);


        if (reviews.isEmpty()) {
            return commonService.errorResponse(ErrorCode.REVIEW_NOT_REGISTRATION.getDescription(), HttpStatus.OK, null);
        }

        // review entity -> dto 변환
        Page<ShopReviewLookupResponseDto> data = reviews.map(review -> {
            Long reviewId = review.getReviewId();
            // 리뷰 이미지 가져오기
            String reviewImgPath = reviewImgRepository.findReviewImgByShopIdAndReviewId(shopId, reviewId);
            // dto에 shop entity 값을 변환하는 과정
            ShopReviewLookupResponseDto dto = new ShopReviewLookupResponseDto(
                    reviewId,
                    review.getContents(),
                    review.getRate(),
                    reviewImgPath,
                    review.getCreatedAt()
            );
            // data 에 dto 반환
            return dto;
        });


        // 리뷰 리스트 가져오기
        List<ShopReviewLookupResponseDto> shopReviewLookupResponseDtoList = data.getContent();


        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(data.getTotalPages())
                                                   .totalElements(data.getTotalElements())
                                                   .pageNo(data.getNumber())
                                                   .isLastPage(data.isLast())
                                                   .build();

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopReviewListLookupResponseDto shopReviewListLookupResponseDto = ShopReviewListLookupResponseDto.builder()
                                                                                                         .shopReviewLookupResponseDto(shopReviewLookupResponseDtoList)
                                                                                                         .paginationDto(paginationDto)
                                                                                                         .build();


        return commonService.successResponse(SuccessCode.SHOP_REVIEW_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopReviewListLookupResponseDto);
    }



}
