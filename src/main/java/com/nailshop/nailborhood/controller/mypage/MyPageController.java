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
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.mypage.MypageService;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
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

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private final MypageService mypageService;
    private final MemberService memberService;
    private final ShopRegistrationService shopRegistrationService;


    // 내가 쓴 리뷰
    @Tag(name = "myPage", description = "myPage API")
    @Operation(summary = "내가 작성한 리뷰 조회", description = "myPage API")
    @GetMapping("/review/inquiry")
    public ResponseEntity<ResultDto<MyReviewListResponseDto>> myReview(@RequestHeader(AUTH) String accessToken,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                       @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                       @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy){
        CommonResponseDto<Object> myReview = mypageService.myReview(accessToken, page, size, sortBy);
        ResultDto<MyReviewListResponseDto> resultDto = ResultDto.in(myReview.getStatus(), myReview.getMessage());
        resultDto.setData((MyReviewListResponseDto) myReview.getData());

        return ResponseEntity.status(myReview.getHttpStatus()).body(resultDto);
    }

    // 찜한 매장 조회
    @Tag(name = "myPage", description = "myPage API")
    @Operation(summary = "내가 찜한 매장 조회", description = "myPage API")
    @GetMapping("/shop/favorite/inquiry")
    public ResponseEntity<ResultDto<MyFavoriteListResponseDto>> myFavorite(@RequestHeader(AUTH) String accessToken,
                                                                           @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                           @RequestParam(value = "size", defaultValue = "10", required = false) int size){
        CommonResponseDto<Object> myFavorite = mypageService.myFavorite(accessToken, page, size);
        ResultDto<MyFavoriteListResponseDto> resultDto = ResultDto.in(myFavorite.getStatus(), myFavorite.getMessage());
        resultDto.setData((MyFavoriteListResponseDto) myFavorite.getData());

        return ResponseEntity.status(myFavorite.getHttpStatus()).body(resultDto);
    }

    // 비밀번호 수정
    @PostMapping("/modifyPassword")
    public ResponseEntity<ResultDto<Void>> modifyPassword(@RequestHeader(AUTH) String accessToken,
                                                          @RequestBody ModPasswordRequestDto modPasswordRequestDto){
        CommonResponseDto<Object> commonResponseDto = memberService.updatePassword(accessToken, modPasswordRequestDto);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    // 프로필 수정
    @PutMapping(consumes = {"multipart/form-data"}, value = "/modProfile")
    public ResponseEntity<ResultDto<Void>> modifyProfile(@RequestHeader(AUTH) String accessToken,
                                                         @RequestPart(value = "file") MultipartFile multipartFile){
        CommonResponseDto<Object> commonResponseDto = memberService.updateProfileImg(accessToken, multipartFile);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    // 비밀번호 수정 전 확인
//    @PostMapping("/passwordCheck")
//    public ResponseEntity<ResultDto<Object>> passwordCheck(@RequestHeader(AUTH) String accessToken,
//                                                           @RequestBody BeforeModPasswordCheckRequestDto beforeModPasswordCheckRequestDto){
//        CommonResponseDto<Object> commonResponseDto = memberService.beforeUpdatePassword(accessToken, beforeModPasswordCheckRequestDto);
//        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
//        result.setData((boolean) commonResponseDto.getData());
//        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
//    }

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
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
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
