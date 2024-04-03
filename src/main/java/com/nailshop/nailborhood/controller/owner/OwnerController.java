package com.nailshop.nailborhood.controller.owner;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.dto.shop.request.StoreAddressSeparationDto;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.artboard.ArtInquiryService;
import com.nailshop.nailborhood.service.owner.OwnerService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.service.shop.owner.ShopModificationService;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ShopStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final ArtInquiryService artInquiryService;
    private final ShopModificationService shopModificationService;
    private final ShopDetailService shopDetailService;
    private final ShopRegistrationService shopRegistrationService;

    // 검색기능이랑 통합
    //사장님 리뷰 검색
    @GetMapping("/owner/review/{shopId}")
    public String getShopReviewList(Model model,
                                    @PathVariable Long shopId,
                                    @RequestParam(value = "keyword", required = false) String keyword,
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

    // 아트 관리
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping("/owner/artboard/manage")
    public String inquiryAllArtRef(@AuthenticationPrincipal MemberDetails memberDetails,
                                   @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "5", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                   @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                   Model model) {

        boolean error = false;

        try {
            CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArtByShopId(memberDetails, page, size, sortBy, category);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
            resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

            model.addAttribute("result", resultDto);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("size", size);
            model.addAttribute("error", error);
        } catch (Exception e) {

            error = true;
            model.addAttribute("error", error);
        }

        return "artboard/art_manage";
    }


    // TODO: user 연결 필요
    // 매장 정보 수정

    @GetMapping("/owner/shop/update/{shopId}")
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

    @PostMapping("/owner/shop/update/{shopId}")
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