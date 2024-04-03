package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.FavoriteResponseDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.FavoriteShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteShopService favoriteShopService;


    // 매장 찜
    @PostMapping("/favorite/{shopId}")
    public ResponseEntity<ResultDto<FavoriteResponseDto>> getShopDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                        @PathVariable Long shopId){
        CommonResponseDto<Object> favoriteShop = favoriteShopService.favoriteShop(shopId, memberDetails);
        ResultDto<FavoriteResponseDto> resultDto = ResultDto.in(favoriteShop.getStatus(), favoriteShop.getMessage());
        resultDto.setData((FavoriteResponseDto) favoriteShop.getData());

        return ResponseEntity.status(favoriteShop.getHttpStatus()).body(resultDto);
    }
}
