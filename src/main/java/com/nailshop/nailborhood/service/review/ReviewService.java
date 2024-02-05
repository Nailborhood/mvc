package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.review.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.ReviewUpdateDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.ReviewRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CommonService commonService;

    // 리뷰 수정
    @Transactional
    public CommonResponseDto<Object> reviewUpdate(Long reviewId, Long shopId,
//                                                  List<MultipartFile> multipartFileList,
                                                  ReviewUpdateDto reviewUpdateDto) {

        // 매장 정보 확인

        // 리뷰 가져오기
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        review.reviewUpdate(reviewUpdateDto.getContents(), reviewUpdateDto.getRate());

        reviewRepository.save(review);

        return commonService.successResponse(SuccessCode.REVIEW_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }


}
