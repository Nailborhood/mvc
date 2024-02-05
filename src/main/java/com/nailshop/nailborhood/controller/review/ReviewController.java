package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.ReviewUpdateDto;
import com.nailshop.nailborhood.service.review.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nailshop")
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 수정
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewUpdate(@PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId,
//                                                      @RequestPart(value = "img") List<MultipartFile> multipartFileList,
                                                      @RequestBody ReviewUpdateDto reviewUpdateDto){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewUpdate(reviewId, shopId, reviewUpdateDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }


}
