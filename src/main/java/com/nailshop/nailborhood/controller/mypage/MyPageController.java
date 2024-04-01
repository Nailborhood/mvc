package com.nailshop.nailborhood.controller.mypage;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import com.nailshop.nailborhood.dto.member.request.BeforeModPasswordCheckRequestDto;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MypageService mypageService;
    private final MemberService memberService;
    private final ShopRegistrationService shopRegistrationService;
    private final ShopRequestLookupService shopRequestLookupService;


    // 내가 쓴 리뷰
    @GetMapping("/review/inquiry")
    public String myReview(Model model,
                           @AuthenticationPrincipal MemberDetails memberDetails,
                           @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                           @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                           @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy){

        Long memberId = memberDetails.getMember().getMemberId();
        boolean error = false;
        try {
            CommonResponseDto<Object> myReview = mypageService.myReview(memberId, page, size, sortBy);
            ResultDto<MyReviewListResponseDto> resultDto = ResultDto.in(myReview.getStatus(), myReview.getMessage());
            resultDto.setData((MyReviewListResponseDto) myReview.getData());

            model.addAttribute("result", resultDto);
        }catch (Exception e){

            error = true;
            model.addAttribute("error", error);
        }

        return "mypage/my_review_list";

    }

    // 찜한 매장 조회
    @GetMapping("/shop/favorite/inquiry")
    public String myFavorite(Model model,
                             @AuthenticationPrincipal MemberDetails memberDetails,
                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                             @RequestParam(value = "size", defaultValue = "10", required = false) int size){

        Long memberId = memberDetails.getMember().getMemberId();
        boolean error = false;
        try {
            CommonResponseDto<Object> myFavorite = mypageService.myFavorite(memberId, page, size);
            ResultDto<MyFavoriteListResponseDto> resultDto = ResultDto.in(myFavorite.getStatus(), myFavorite.getMessage());
            resultDto.setData((MyFavoriteListResponseDto) myFavorite.getData());

            model.addAttribute("result", resultDto);
        }catch (Exception e){

            error = true;
            model.addAttribute("error", error);
        }


        return "mypage/my_fav_shop_list";

    }

    // 비밀번호 수정
    @PostMapping("/modifyPassword")
    public ResponseEntity<ResultDto<Void>> modifyPassword(@RequestHeader(AUTH) String accessToken,
                                                          @RequestBody ModPasswordRequestDto modPasswordRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.updatePassword(accessToken, modPasswordRequestDto);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(result);
    }

    // 프로필 수정
    @PutMapping(consumes = {"multipart/form-data"}, value = "/modProfile")
    public ResponseEntity<ResultDto<Void>> modifyProfile(@RequestHeader(AUTH) String accessToken,
                                                         @RequestPart(value = "file") MultipartFile multipartFile) {
        CommonResponseDto<Object> commonResponseDto = memberService.updateProfileImg(accessToken, multipartFile);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(result);
    }

    // 비밀번호 수정 전 확인
    @PostMapping("/passwordCheck")
    public ResponseEntity<ResultDto<Object>> passwordCheck(@RequestHeader(AUTH) String accessToken,
                                                           @RequestBody BeforeModPasswordCheckRequestDto beforeModPasswordCheckRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.beforeUpdatePassword(accessToken, beforeModPasswordCheckRequestDto);
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((boolean) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .body(result);
    }

    //  내 정보 확인
    @GetMapping("/myInfo")
    public String modifyInfoPage(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
        model.addAttribute("memberNickname", nicknameSpace);
        Long loginId = memberDetails.getMember().getMemberId();
        CommonResponseDto<Object> commonResponseDto = memberService.findMyInfo(loginId);
//        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        model.addAttribute("memberInfo", commonResponseDto.getData());
        return "mypage/modify_info_form";
    }

    // 내 정보 수정 - 작업중
    @PostMapping("/modMyInfo")
    public ResponseEntity<ResultDto<MemberInfoDto>> modMyInfo(@RequestHeader(AUTH) String accessToken,
                                                              @RequestBody ModMemberInfoRequestDto modMemberInfoRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.updateMyInfo(accessToken, modMemberInfoRequestDto);
        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((MemberInfoDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(result);
    }

//    //  내 정보 확인
//    @GetMapping("/myInfo")
//    public ResponseEntity<ResultDto<MemberInfoDto>> getMyProfile(@RequestHeader(AUTH) String accessToken) {
//        CommonResponseDto<Object> commonResponseDto = memberService.findMyInfo(accessToken);
//        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
//        result.setData((MemberInfoDto) commonResponseDto.getData());
//        return ResponseEntity.status(commonResponseDto.getHttpStatus())
//                             .body(result);
//    }

    //TODO: accessToken or session 연결 필요
    // 매장 신청
    @GetMapping("/owner/shop/request")
    public String requestShop(Model model,
                              ShopRegistrationRequestDto shopRegistrationRequestDto) {

        // 이미 신청한 기록이 존재
/*        if(shopRegistrationService.checkExistingOwner()){
            model.addAttribute("errorCode",ErrorCode.OWNER_ALREADY_EXIST);

        }*/
        StoreAddressSeparationListDto storeAddressSeparationListDtoList =shopRegistrationService.findAddress();
        model.addAttribute("requestDto", shopRegistrationRequestDto);
        model.addAttribute("addressDto", storeAddressSeparationListDtoList);
        return "request/request_shop_registration";
    }

    //TODO: accessToken or session 연결 필요
    @PostMapping("/owner/shop/request")
    public String requestShop(@RequestPart(value = "file") List<MultipartFile> multipartFileList,
                              @RequestPart(value = "certificateFile") List<MultipartFile> fileList,
                              @ModelAttribute StoreAddressSeparationDto storeAddressSeparationDto,
                              @ModelAttribute ShopRegistrationRequestDto shopRegistrationRequestDto,
                              RedirectAttributes redirectAttributes) {


        try {

            shopRegistrationService.updateAddressInfo(shopRegistrationRequestDto, storeAddressSeparationDto);
            CommonResponseDto<Object> commonResponseDto = shopRegistrationService.registerShop(multipartFileList, fileList, shopRegistrationRequestDto);
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

    //TODO: accessToken or session 연결 필요 (memberId 필요)

    // 매장 신청 조회
    @GetMapping("/owner/shop/request/list")
    public String requestShopList(Model model) {
        try {

            CommonResponseDto<Object> myShop = shopRequestLookupService.getShopRequest();
            model.addAttribute("myShop", myShop.getData());
            return "request/request_shop_registration_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.SHOP_REQUEST_NOT_FOUND);
            return "request/request_shop_registration_list";
        }
    }

    // 회원탈퇴
    @GetMapping("/dropoutProc")
    public String memberDropOut(@AuthenticationPrincipal MemberDetails memberDetails) {
        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
        Long id = memberDetails.getMember().getMemberId();
        CommonResponseDto<Object> commonResponseDto = memberService.deleteMember(id);
        return "redirect:/logout";
    }

    // 회원 탈퇴 페이지
    @GetMapping("/dropout")
    public String dropoutPage(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
        model.addAttribute("memberNickname", nicknameSpace);
        return "mypage/drop_out_form";
    }

    // 로그아웃 페이지
    @GetMapping("/logout")
    public String logoutPage(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
        model.addAttribute("memberNickname", nicknameSpace);
        return "mypage/logout_form";
    }

}


