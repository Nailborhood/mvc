package com.nailshop.nailborhood.controller.mypage;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.mypage.MyFavoriteListResponseDto;
import com.nailshop.nailborhood.dto.mypage.MyReviewListResponseDto;
import com.nailshop.nailborhood.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MypageService mypageService;

    // 내가 쓴 리뷰
    @GetMapping("/mypage/review/inquiry")
    public ResponseEntity<ResultDto<MyReviewListResponseDto>> myReview(@RequestHeader("Authorization") String accessToken,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                       @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                       @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy){
        CommonResponseDto<Object> myReview = mypageService.myReview(accessToken, page, size, sortBy);
        ResultDto<MyReviewListResponseDto> resultDto = ResultDto.in(myReview.getStatus(), myReview.getMessage());
        resultDto.setData((MyReviewListResponseDto) myReview.getData());

        return ResponseEntity.status(myReview.getHttpStatus()).body(resultDto);
    }

    // 찜한 매장 조회
    @GetMapping("/mypage/shop/favorite/inquiry")
    public ResponseEntity<ResultDto<MyFavoriteListResponseDto>> myFavorite(@RequestHeader("Authorization") String accessToken,
                                                                           @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                           @RequestParam(value = "size", defaultValue = "10", required = false) int size){
        CommonResponseDto<Object> myFavorite = mypageService.myFavorite(accessToken, page, size);
        ResultDto<MyFavoriteListResponseDto> resultDto = ResultDto.in(myFavorite.getStatus(), myFavorite.getMessage());
        resultDto.setData((MyFavoriteListResponseDto) myFavorite.getData());

        return ResponseEntity.status(myFavorite.getHttpStatus()).body(resultDto);
    }
}
