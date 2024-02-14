package com.nailshop.nailborhood.controller.search;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.example.ExampleDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nailborhood")
public class SearchController {

    private final SearchService searchService;

    // 리뷰 검색
    @Tag(name = "search", description = "search API")
    @Operation(summary = "리뷰 검색", description = "search API")
    @GetMapping("/search/review")
    public ResponseEntity<ResultDto<ReviewListResponseDto>> searchReviewInquiry(@RequestParam(value = "keyword") String keyword,
                                                                                @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                                @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy){
        CommonResponseDto<Object> commonResponseDto = searchService.searchReviewInquiry(keyword, page, size, sortBy);
        ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        resultDto.setData((ReviewListResponseDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    // 아트판 검색
    @Tag(name = "search", description = "search API")
    @Operation(summary = "아트 검색", description = "search API")
    @GetMapping("/search/artRef")
    public ResponseEntity<ResultDto<ArtListResponseDto>> searchArtRefInquiry(@RequestParam(value = "keyword") String keyword,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                             @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy){
        CommonResponseDto<Object> commonResponseDto = searchService.searchArtRefInquiry(keyword, page, size, sortBy);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        resultDto.setData((ArtListResponseDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    // 가게 검색
    @GetMapping("/search/shop")
    public ResponseEntity<ResultDto<ShopListResponseDto>> searchShopInquiry (@RequestParam(value = "keyword") String keyword,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                             @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy){
        CommonResponseDto<Object> commonResponseDto = searchService.searchShopInquiry(keyword, page, size, sortBy);
        ResultDto<ShopListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        resultDto.setData((ShopListResponseDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }
}
