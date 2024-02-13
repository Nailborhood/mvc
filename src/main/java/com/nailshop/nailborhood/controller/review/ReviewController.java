package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.request.ReviewReportDto;
import com.nailshop.nailborhood.dto.review.request.ReviewUpdateDto;
import com.nailshop.nailborhood.service.review.ReviewService;
import com.nailshop.nailborhood.dto.review.request.ReviewRegistrationRequestDto;
import com.nailshop.nailborhood.service.review.ReviewRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRegistrationService reviewRegistrationService;

    // TODO: accesstoken 연결
    @Tag(name = "review", description = "review API")
    @Operation(summary = "리뷰 등록", description = "review API")
    // 매장 정보 등록
    @PostMapping(consumes = {"multipart/form-data"}, value = "{shopId}/review/registration")
    public ResponseEntity<ResultDto<Void>> registerShop(@PathVariable Long shopId,
                                                        @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data") ReviewRegistrationRequestDto reviewRegistrationRequestDto) {
        CommonResponseDto<Object> commonResponseDto = reviewRegistrationService.registerReview(shopId,multipartFileList,reviewRegistrationRequestDto );
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .body(resultDto);
    }

    //리뷰 수정
    @PutMapping("/review/update/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewUpdate(@RequestHeader("Authorization") String accessToken,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId,
                                                        @RequestPart(value = "img") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data") ReviewUpdateDto reviewUpdateDto){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewUpdate(accessToken, reviewId, shopId, multipartFileList,reviewUpdateDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    // 리뷰 신고
    @PostMapping("/review/report/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewReport(@RequestHeader("Authorization") String accessToken,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId,
                                                        @RequestBody ReviewReportDto reviewReportDto){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewReport(accessToken, reviewId, shopId,reviewReportDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    // 리뷰 삭제
    @DeleteMapping("/mypage/review/{reviewId}")
    public ResponseEntity<ResultDto<Void>> reviewDelete(@RequestHeader("Authorization") String accessToken,
                                                        @PathVariable Long reviewId,
                                                        @RequestParam(value = "shopId") Long shopId){
        CommonResponseDto<Object> commonResponseDto = reviewService.reviewDelete(accessToken, reviewId, shopId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }


}
