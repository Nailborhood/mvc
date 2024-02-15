package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewLike;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewLikeResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.review.ReviewLikeRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewLikeService {

    private final CommonService commonService;
    private final TokenProvider tokenProvider;
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public CommonResponseDto<Object> reviewLike(String accessToken, Long reviewId, Long shopId) {

        Long memberId = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 매장 존재 여부
        shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 존재 여부
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 원래 좋아요가 있었는지 확인
        ReviewLike existingLike = reviewLikeRepository.findMemberAndReview(memberId, reviewId);

        if (existingLike == null) {
            // 첫 좋아요

            ReviewLike newReviewLike = ReviewLike.builder()
                    .status(true)
                    .member(member)
                    .review(review)
                    .build();

            reviewLikeRepository.save(newReviewLike);
            reviewRepository.likeCntIncrease(reviewId);

            ReviewLikeResponseDto reviewLikeResponseDto = ReviewLikeResponseDto.builder()
                                                                                .reviewLikeStatus(true)
                                                                                .build();

            return commonService.successResponse(SuccessCode.REVIEW_LIKE_SUCCESS.getDescription(), HttpStatus.OK, reviewLikeResponseDto);
        } else {

            // 좋아요 한적이 있는경우 -> 좋아요 상태 변경
            Long likeId = existingLike.getReviewLikeId();

            // true -> false
            if (existingLike.getStatus()) {

                reviewRepository.likeCntDecrease(reviewId);
                reviewLikeRepository.updateStatus(likeId, false);

                ReviewLikeResponseDto reviewLikeResponseDto = ReviewLikeResponseDto.builder()
                        .reviewLikeStatus(false)
                        .build();

                return commonService.successResponse(SuccessCode.REVIEW_LIKE_CANCEL_SUCCESS.getDescription(), HttpStatus.OK, reviewLikeResponseDto);
            }else {
                // false -> true

                reviewRepository.likeCntIncrease(reviewId);
                reviewLikeRepository.updateStatus(likeId, true);

                ReviewLikeResponseDto reviewLikeResponseDto = ReviewLikeResponseDto.builder()
                        .reviewLikeStatus(true)
                        .build();

                return commonService.successResponse(SuccessCode.REVIEW_LIKE_SUCCESS.getDescription(), HttpStatus.OK, reviewLikeResponseDto);

            }


        }
    }
}
