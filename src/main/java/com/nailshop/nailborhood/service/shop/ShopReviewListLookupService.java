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
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
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
    private final ShopRepository shopRepository;
    private final ShopImgRepository shopImgRepository;

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
            ShopAndReviewLookUpResponseDto dto = new ShopAndReviewLookUpResponseDto(
                    reviewId,
                    review.getContents(),
                    review.getRate(),
                    reviewImgPath,
                    review.getCreatedAt(),
                    review.getCustomer().getMember().getNickname(),
                    shop.getName(),
                    shop.getAddress(),
                    shop.getOpentime(),
                    shop.getRateAvg(),
                    shop.getReviewCnt(),
                    shop.getFavoriteCnt(),
                    shopMainImgPath
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

}
