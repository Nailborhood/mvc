package com.nailshop.nailborhood.controller.owner;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.dto.shop.request.StoreAddressSeparationDto;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.alarm.AlarmService;
import com.nailshop.nailborhood.service.artboard.ArtInquiryService;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.owner.OwnerService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.service.shop.owner.ShopModificationService;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ShopStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final ArtInquiryService artInquiryService;
    private final ShopModificationService shopModificationService;
    private final ShopDetailService shopDetailService;
    private final ShopRegistrationService shopRegistrationService;
    private final MemberService memberService;
    private final CategoryRepository categoryRepository;
    private final AlarmService alarmService;


    // 검색기능이랑 통합
    // 사장님 리뷰 검색
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/owner/review")
    public String getShopReviewList(Model model,
                                    Authentication authentication,
                                    @AuthenticationPrincipal MemberDetails memberDetails,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                    @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {


        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);


        boolean error = false;

        try {
            CommonResponseDto<Object> shopReview = ownerService.getAllReviewListByShopId(keyword, page, size, criteria, sort, sessionDto.getId());

            model.addAttribute("reviewList", shopReview.getData());
            model.addAttribute("error", error);

        } catch (NotFoundException e) {

            error = true;
            model.addAttribute("error", error);
        }

        return "owner/review_manage";

    }

    // 아트 관리
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/owner/artboard/manage")
    public String inquiryAllArtRef(@AuthenticationPrincipal MemberDetails memberDetails,
                                   Authentication authentication,
                                   @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "5", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   Model model) {
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        boolean error = false;

        try {
            CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArtByShopId(memberDetails, page, size, sortBy, keyword);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
            resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

            model.addAttribute("result", resultDto);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("keyword", keyword);
            model.addAttribute("error", error);
        } catch (Exception e) {

            error = true;
            model.addAttribute("error", error);
        }

        return "artboard/art_manage";
    }


    // 매장 정보 수정 ( 매장 정보 불러오기 )
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/owner/shop/update")
    public String updateShop(Model model,
                             @AuthenticationPrincipal MemberDetails memberDetails,
                             Authentication authentication) {


        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);


        CommonResponseDto<Object> shopDetail = shopDetailService.getMyShopDetail(sessionDto.getId());
        ResultDto<MyShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
        resultDto.setData((MyShopDetailListResponseDto) shopDetail.getData());

        StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();


        model.addAttribute("shopDto", resultDto);
        model.addAttribute("addressDto", storeAddressSeparationListDtoList);
        return "owner/owner_shop_update";
    }


    // 매장 정보 수정
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PostMapping("/owner/shop/update")
    public String updateShop(Model model,
                             @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                             @ModelAttribute ShopModifiactionRequestDto shopModifiactionRequestDto,
                             @ModelAttribute StoreAddressSeparationDto storeAddressSeparationDto,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal MemberDetails memberDetails,
                             Authentication authentication) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        Owner owner = ownerService.getOwnerInfo(sessionDto.getId());

        Long shopId = owner.getShop().getShopId();

        try {

            shopModificationService.updateAddressInfo(shopModifiactionRequestDto, storeAddressSeparationDto);
            CommonResponseDto<Object> commonResponseDto = shopModificationService.updateShop(sessionDto.getId(), multipartFileList, shopModifiactionRequestDto);
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

    // 내 매장 상세 조회
    // 매장상세에 heartStatus때문에 memberDetails 추가함
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/owner/shopDetail")
    public String getShopDetail(Model model,
                                @AuthenticationPrincipal MemberDetails memberDetails,
                                Authentication authentication) {
      
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        Owner owner = ownerService.getOwnerInfo(sessionDto.getId());
        Long shopId = owner.getShop().getShopId();
        CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetail(shopId,sessionDto.getId());
        ResultDto<ShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
        resultDto.setData((ShopDetailListResponseDto) shopDetail.getData());

        model.addAttribute("resultDto", resultDto);


        Member receiver = alarmService.getOwnerInfo(shopId);
        model.addAttribute("receiver", receiver);

        return "shop/shop_detail";
    }


    // enum 타임리프로 리턴
    @ModelAttribute("shopStatus")
    public ShopStatus[] shopStatuses() {
        return Stream.of(ShopStatus.values())
                     .filter(status -> status == ShopStatus.BEFORE_OPEN || status == ShopStatus.OPEN)
                     .toArray(ShopStatus[]::new);
    }

}