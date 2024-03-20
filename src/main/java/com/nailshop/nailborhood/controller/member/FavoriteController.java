package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.FavoriteResponseDto;
import com.nailshop.nailborhood.service.member.FavoriteShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteShopService favoriteShopService;


    // 매장 찜
    @PostMapping("/favorite/{shopId}")
    public ResponseEntity<ResultDto<FavoriteResponseDto>> getShopDetail(/*@RequestHeader(AUTH) String accessToken,*/
                                                                        @PathVariable Long shopId){
        CommonResponseDto<Object> favoriteShop = favoriteShopService.favoriteShop(shopId/*,accessToken*/);
        ResultDto<FavoriteResponseDto> resultDto = ResultDto.in(favoriteShop.getStatus(), favoriteShop.getMessage());
        resultDto.setData((FavoriteResponseDto) favoriteShop.getData());

        return ResponseEntity.status(favoriteShop.getHttpStatus()).body(resultDto);
    }
}
