package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.domain.review.ReviewLike;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.review.response.ReviewLikeResponseDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.review.ReviewLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;
    private final MemberService memberService;

    // 리뷰 좋아요
    @PostMapping("/like/review/{reviewId}")
    public ResponseEntity<ResultDto<ReviewLikeResponseDto>> reviewLike(Authentication authentication,
                                                                       @AuthenticationPrincipal MemberDetails memberDetails,
                                                                       @PathVariable Long reviewId,
                                                                       @RequestParam(value = "shopId") Long shopId){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        CommonResponseDto<Object> reviewLikeOn = reviewLikeService.reviewLike(sessionDto.getId(), reviewId, shopId);
        ResultDto<ReviewLikeResponseDto> resultDto = ResultDto.in(reviewLikeOn.getStatus(), reviewLikeOn.getMessage());
        resultDto.setData((ReviewLikeResponseDto)reviewLikeOn.getData());

        return ResponseEntity.status(reviewLikeOn.getHttpStatus()).body(resultDto);
    }
}
