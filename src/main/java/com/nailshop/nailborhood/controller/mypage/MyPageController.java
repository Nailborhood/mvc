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
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.mypage.MypageService;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    @PostMapping("/passwordCheck")
    public ResponseEntity<ResultDto<Object>> passwordCheck(@RequestHeader(AUTH) String accessToken,
                                                           @RequestBody BeforeModPasswordCheckRequestDto beforeModPasswordCheckRequestDto){
        CommonResponseDto<Object> commonResponseDto = memberService.beforeUpdatePassword(accessToken, beforeModPasswordCheckRequestDto);
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((boolean) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    // 내 정보 수정
    @PutMapping("/modMyInfo")
    public ResponseEntity<ResultDto<MemberInfoDto>> modMyInfo(@RequestHeader(AUTH) String accessToken,
                                                              @RequestBody ModMemberInfoRequestDto modMemberInfoRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.updateMyInfo(accessToken, modMemberInfoRequestDto);
        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((MemberInfoDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    //  내 정보 확인
    @GetMapping("/myInfo")
    public ResponseEntity<ResultDto<MemberInfoDto>> getMyProfile(@RequestHeader(AUTH) String accessToken) {
        CommonResponseDto<Object> commonResponseDto = memberService.findMyInfo(accessToken);
        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((MemberInfoDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }



    // 매장 신청
    @GetMapping("/owner/shopRequest")
    public String requestShop(Model model,
                                ShopRegistrationRequestDto shopRegistrationRequestDto){
        model.addAttribute("requestDto",shopRegistrationRequestDto);
        return "mypage_shopRequest";
    }

    @PostMapping("/owner/shopRequest")
    public String requestShop(@RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "certificateFile") List<MultipartFile> fileList,
                                                        @ModelAttribute ShopRegistrationRequestDto shopRegistrationRequestDto) {
        try{
            CommonResponseDto<Object> commonResponseDto = shopRegistrationService.registerShop(multipartFileList,fileList, shopRegistrationRequestDto);
            ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

            //TODO: 심사 중인 페이지 필요 ( 매장 신청 리스트 보여지면서 "심사중")
            return "redirect:/";
        } catch (NotFoundException e) {

            return "mypage_shopRequest";
        }

    }

    // 매장 신청 조회
/*    @GetMapping("/owner/shopRequest/list")
    public String requestShopList(Model model
                                  ){
        try{
            CommonResponseDto<Object> shopRequestList =
        }catch (NotFoundException e){

        }

        try {
            CommonResponseDto<Object> allShopRequestList = shopRequestLookupAdminService.getAllShopRequest(keyword, page, size, criteria, sort);
            model.addAttribute("requestList", allShopRequestList.getData());
            return "admin/admin_shopRequest_list";
        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.SHOP_REQUEST_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_shopRequest_list";
        }
    }*/

}
