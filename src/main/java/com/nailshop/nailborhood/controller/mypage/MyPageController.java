package com.nailshop.nailborhood.controller.mypage;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.member.request.ModMemberInfoRequestDto;
import com.nailshop.nailborhood.dto.member.request.ModPasswordRequestDto;
import com.nailshop.nailborhood.dto.mypage.MyFavoriteListResponseDto;
import com.nailshop.nailborhood.dto.mypage.MyReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.request.StoreAddressSeparationDto;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.mypage.MypageService;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
import com.nailshop.nailborhood.service.shop.owner.ShopRequestLookupService;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MypageService mypageService;
    private final MemberService memberService;
    private final ShopRegistrationService shopRegistrationService;
    private final ShopRequestLookupService shopRequestLookupService;


    // 내가 쓴 리뷰
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/review/inquiry")
    public String myReview(Model model,
                           Authentication authentication,
                           @AuthenticationPrincipal MemberDetails memberDetails,
                           @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                           @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                           @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy) {


        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);


        try {
            CommonResponseDto<Object> myReview = mypageService.myReview(sessionDto.getId(), page, size, sortBy);
            ResultDto<MyReviewListResponseDto> resultDto = ResultDto.in(myReview.getStatus(), myReview.getMessage());
            resultDto.setData((MyReviewListResponseDto) myReview.getData());

            model.addAttribute("result", resultDto);
        } catch (NotFoundException e) {
            model.addAttribute("ReviewErrorCode", ErrorCode.REVIEW_NOT_FOUND);
        }

        return "mypage/my_review_list";

    }

    // 찜한 매장 조회
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/shop/favorite/inquiry")
    public String myFavorite(Model model,
                             Authentication authentication,
                             @AuthenticationPrincipal MemberDetails memberDetails,
                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                             @RequestParam(value = "size", defaultValue = "10", required = false) int size) {


        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);


        try {
            CommonResponseDto<Object> myFavorite = mypageService.myFavorite(sessionDto.getId(), page, size);
            ResultDto<MyFavoriteListResponseDto> resultDto = ResultDto.in(myFavorite.getStatus(), myFavorite.getMessage());
            resultDto.setData((MyFavoriteListResponseDto) myFavorite.getData());


            model.addAttribute("result", resultDto);

        } catch (NotFoundException e) {

            model.addAttribute("ErrorCode", ErrorCode.SHOP_FAVORITE_EMPTY);

        }


        return "mypage/my_fav_shop_list";

    }

    //내가 북마크한 아트
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/art/bookmark/inquiry")
    public String myBookmark(Model model,
                             Authentication authentication,
                             @AuthenticationPrincipal MemberDetails memberDetails,
                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                             @RequestParam(value = "size", defaultValue = "20", required = false) int size) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        try {
            CommonResponseDto<Object> myBookmark = mypageService.myBookmark(sessionDto.getId(), page, size);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(myBookmark.getStatus(), myBookmark.getMessage());
            resultDto.setData((ArtListResponseDto) myBookmark.getData());


            model.addAttribute("result", resultDto);

        } catch (NotFoundException e) {

            model.addAttribute("ArtErrorCode", ErrorCode.ART_BOOKMARK_EMPTY);

        }


        return "mypage/my_bookmark_list";

    }

    // 비밀번호 수정 페이지
    @GetMapping("/password")
    public String modPasswordPage(Authentication authentication,
                                  @AuthenticationPrincipal MemberDetails memberDetails,
                                  Model model,
                                  ModPasswordRequestDto modPasswordRequestDto) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        // TODO 구글 로그인은 비밀번호 처리를 어떻게 할 것인가?
        model.addAttribute("modPasswordRequestDto", modPasswordRequestDto);
        return "mypage/modify_password_form";
    }

    // 비밀번호 수정
    @PostMapping("/modifyPassword")
    @ResponseBody
    public String modifyPassword(@AuthenticationPrincipal MemberDetails memberDetails,
                                 @ModelAttribute ModPasswordRequestDto modPasswordRequestDto) {
        Long id = memberDetails.getMember()
                               .getMemberId();
        CommonResponseDto<Object> commonResponseDto = memberService.updatePassword(id, modPasswordRequestDto);
        if (commonResponseDto.getHttpStatus()
                             .is2xxSuccessful()) return "redirect:/mypage/myInfo";
        else return ""; // TODO 에러처리 추가 필요
    }

    // 프로필 수정
    @PostMapping(consumes = {"multipart/form-data"}, value = "/modProfile")
    public String modifyProfile(Authentication authentication,
                                @AuthenticationPrincipal MemberDetails memberDetails,
                                Model model,
                                @RequestPart(value = "file") MultipartFile multipartFile) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        CommonResponseDto<Object> commonResponseDto = memberService.updateProfileImg(sessionDto.getId(), multipartFile);
        return "redirect:/user";
    }

    //  내 정보 확인
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInfo")
    public String modifyInfoPage(Authentication authentication,
                                 @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        CommonResponseDto<Object> commonResponseDto = memberService.findMyInfo(sessionDto.getId());
        model.addAttribute("memberInfo", commonResponseDto.getData());
        return "mypage/modify_info_form";
    }

    @PostMapping("/modMyInfo")
    public String modMyInfo(Authentication authentication,
                            @AuthenticationPrincipal MemberDetails memberDetails,
                            ModMemberInfoRequestDto modMemberInfoRequestDto) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        CommonResponseDto<Object> commonResponseDto = memberService.updateMyInfo(sessionDto.getId(), modMemberInfoRequestDto);
        System.out.println(commonResponseDto.getMessage());
        return "redirect:/mypage/myInfo";
    }


    // 매장 신청
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/owner/shop/request")
    public String requestShop(Model model,
                              @AuthenticationPrincipal MemberDetails memberDetails,
                              Authentication authentication,
                              ShopRegistrationRequestDto shopRegistrationRequestDto) {


        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        // 이미 신청한 기록이 존재
        if (!shopRegistrationService.checkExistingOwner(sessionDto.getId())) {
            model.addAttribute("errorCode", ErrorCode.OWNER_ALREADY_EXIST);

        }


        StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();
        model.addAttribute("requestDto", shopRegistrationRequestDto);
        model.addAttribute("addressDto", storeAddressSeparationListDtoList);
        return "request/request_shop_registration";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/owner/shop/request")
    public String requestShop(@RequestPart(value = "file") List<MultipartFile> multipartFileList,
                              @RequestPart(value = "certificateFile") List<MultipartFile> fileList,
                              @ModelAttribute StoreAddressSeparationDto storeAddressSeparationDto,
                              @ModelAttribute ShopRegistrationRequestDto shopRegistrationRequestDto,
                              @AuthenticationPrincipal MemberDetails memberDetails,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        try {

            shopRegistrationService.updateAddressInfo(shopRegistrationRequestDto, storeAddressSeparationDto);
            CommonResponseDto<Object> commonResponseDto = shopRegistrationService.registerShop(sessionDto.getId(), multipartFileList, fileList, shopRegistrationRequestDto);
            ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());
            // 마이페이지 - 매장 신청 목록으로 이동
            return "redirect:/mypage/owner/shop/request/list";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorCode", ErrorCode.OWNER_ALREADY_EXIST);
            // 매장 신청 페이지로 이동
            return "request/request_shop_registration";
        }

    }


    // 매장 신청 조회
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_USER')")
    @GetMapping("/owner/shop/request/list")
    public String requestShopList(Model model, @AuthenticationPrincipal MemberDetails memberDetails, Authentication authentication) {


        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        try {

            CommonResponseDto<Object> myShop = shopRequestLookupService.getShopRequest(sessionDto.getId());
            model.addAttribute("myShop", myShop.getData());
            return "request/request_shop_registration_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.SHOP_REQUEST_NOT_FOUND);
            return "request/request_shop_registration_list";
        }
    }

    // 회원탈퇴
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/dropoutProc")
    public String memberDropOut(Authentication authentication,
                                @AuthenticationPrincipal MemberDetails memberDetails) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        CommonResponseDto<Object> commonResponseDto = memberService.deleteMember(sessionDto.getId());
        return "redirect:/logout";
    }

    // 회원 탈퇴 페이지
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/dropout")
    public String dropoutPage(Authentication authentication,
                              @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        return "mypage/drop_out_form";
    }

    // 로그아웃 페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public String logoutPage(Authentication authentication,
                             @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        return "mypage/logout_form";
    }

}


