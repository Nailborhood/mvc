package com.nailshop.nailborhood.controller.search;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.service.search.SearchService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final CategoryRepository categoryRepository;

    // 리뷰 검색
    @GetMapping("/search/review")
    public String searchReviewInquiry(Model model,
                                      @RequestParam(value = "keyword",required = false) String keyword,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                      @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy){
        try {
            CommonResponseDto<Object> commonResponseDto = searchService.searchReviewInquiry(keyword, page, size, sortBy);
            ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

            model.addAttribute("result", resultDto);

            return "review/review_list";

        } catch (NotFoundException e){

            model.addAttribute("errorCode", ErrorCode.REVIEW_NOT_FOUND);

            return "review/review_list";
        }
    }

    // 아트판 검색
    @Tag(name = "search", description = "search API")
    @Operation(summary = "아트 검색", description = "search API")
    @GetMapping("/search/artRef")
    public String searchArtRefInquiry(@RequestParam(value = "keyword") String keyword,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                      @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                      Model model){
        boolean error = false;

        try {
            CommonResponseDto<Object> commonResponseDto = searchService.searchArtRefInquiry(keyword, page, size, sortBy);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
            resultDto.setData((ArtListResponseDto) commonResponseDto.getData());

            model.addAttribute("result", resultDto);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("error", error);
        } catch (Exception e) {
            error = true;
            model.addAttribute("error", error);
        }

        return "artboard/art_list";
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
