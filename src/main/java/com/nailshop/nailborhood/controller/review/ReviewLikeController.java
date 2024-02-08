package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.domain.review.ReviewLike;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ReviewLikeResponseDto;
import com.nailshop.nailborhood.service.review.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nailshop")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/like/review/{reviewId}")
    public ResponseEntity<ResultDto<ReviewLikeResponseDto>> reviewLike(@PathVariable Long reviewId,
                                                                       @RequestParam(value = "shopId") Long shopId,
                                                                       @RequestParam(value = "memberId") Long memberId){
        CommonResponseDto<Object> reviewLikeOn = reviewLikeService.reviewLike(reviewId, shopId, memberId);
        ResultDto<ReviewLikeResponseDto> resultDto = ResultDto.in(reviewLikeOn.getStatus(), reviewLikeOn.getMessage());
        resultDto.setData((ReviewLikeResponseDto)reviewLikeOn.getData());

        return ResponseEntity.status(reviewLikeOn.getHttpStatus()).body(resultDto);
    }
}
