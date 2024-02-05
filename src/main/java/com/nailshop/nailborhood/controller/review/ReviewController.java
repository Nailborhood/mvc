package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.request.ReviewRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.service.review.ReviewRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailshop")
public class ReviewController {

    private final ReviewRegistrationService reviewRegistrationService;

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
}
