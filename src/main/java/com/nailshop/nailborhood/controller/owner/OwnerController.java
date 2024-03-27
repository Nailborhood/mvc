package com.nailshop.nailborhood.controller.owner;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.dto.shop.request.StoreAddressSeparationDto;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.artboard.ArtInquiryService;
import com.nailshop.nailborhood.service.owner.OwnerService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.service.shop.owner.ShopModificationService;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ShopStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final ArtInquiryService artInquiryService;
    private final ShopModificationService shopModificationService;
    private final ShopDetailService shopDetailService;
    private final ShopRegistrationService shopRegistrationService;

    // 검색기능이랑 통합
    @GetMapping("/owner/review/{shopId}")
    public String getShopReviewList(Model model,
                                    @PathVariable Long shopId,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                    @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> shopReview = ownerService.getAllReviewListByShopId(keyword, page, size, criteria, sort, shopId);
        model.addAttribute("reviewList", shopReview.getData());

        return "owner/review_manage";
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 관리", description = "owner API")
    @GetMapping("/owner/artboard/manage")
    public String inquiryAllArtRef(/*@RequestHeader(AUTH) String accessToken,*/
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
            @RequestParam(value = "category", defaultValue = "", required = false) String category,
            Model model) {
        CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArtByShopId(/*accessToken, */page, size, sortBy, category);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
        resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

        model.addAttribute("result", resultDto);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", size);

        return "artboard/art_manage";
    }


    // TODO: user 연결 필요
    // 매장 정보 수정

    @GetMapping("/owner/update/{shopId}")
    public String updateShop(Model model,
                             @PathVariable Long shopId) {
        CommonResponseDto<Object> shopDetail = shopDetailService.getMyShopDetail(shopId);
        ResultDto<MyShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
        resultDto.setData((MyShopDetailListResponseDto) shopDetail.getData());

        StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();

        model.addAttribute("shopDto", resultDto);
        model.addAttribute("addressDto", storeAddressSeparationListDtoList);
        return "owner/owner_shop_update";
    }

    @PostMapping("/owner/update/{shopId}")
    public String updateShop(Model model,
                             @PathVariable Long shopId,
                             @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                             @ModelAttribute ShopModifiactionRequestDto shopModifiactionRequestDto,
                             @ModelAttribute StoreAddressSeparationDto storeAddressSeparationDto,
                             RedirectAttributes redirectAttributes) {
        try {
            shopModificationService.updateAddressInfo(shopModifiactionRequestDto, storeAddressSeparationDto);
            CommonResponseDto<Object> commonResponseDto = shopModificationService.updateShop(shopId, multipartFileList, shopModifiactionRequestDto);
            ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());
            redirectAttributes.addAttribute("shopId", shopId);
            // 매장 상세 페이지로 이동
            return "redirect:/shopDetail/{shopId}";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorCode", ErrorCode.SHOP_UPDATE_FAIL);
            // 매장 수정 페이지로 이동
            return "owner/owner_shop_update";
        }


    }

    // enum 타임리프로 리턴
    @ModelAttribute("shopStatus")
    public ShopStatus[] shopStatuses(){
        return ShopStatus.values();
    }
}