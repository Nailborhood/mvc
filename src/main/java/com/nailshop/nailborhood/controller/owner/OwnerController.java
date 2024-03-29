package com.nailshop.nailborhood.controller.owner;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.artboard.ArtInquiryService;
import com.nailshop.nailborhood.service.owner.OwnerService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final ArtInquiryService artInquiryService;

    // 검색기능이랑 통합
    //사장님 리뷰 검색
    @GetMapping("/owner/review/{shopId}")
    public String getShopReviewList(Model model,
                                    @PathVariable Long shopId,
                                    @RequestParam(value = "keyword",required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                    @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort){
        try {
            CommonResponseDto<Object> shopReview = ownerService.getAllReviewListByShopId(shopId, keyword, page, size, criteria, sort);
            model.addAttribute("reviewList", shopReview.getData());

            return "owner/review_manage";

        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.REVIEW_NOT_FOUND);

            return "owner/review_manage";
        }

    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 관리", description = "owner API")
    @GetMapping("/owner/artboard/manage")
    public String inquiryAllArtRef(/*@RequestHeader(AUTH) String accessToken,*/
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
            @RequestParam(value = "category", defaultValue = "", required = false) String category,
            Model model){
        CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArtByShopId(/*accessToken, */page, size, sortBy, category);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
        resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

        model.addAttribute("result", resultDto);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", size);

        return "artboard/art_manage";
    }
}