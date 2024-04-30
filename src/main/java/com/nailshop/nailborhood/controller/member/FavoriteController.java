package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.member.response.FavoriteResponseDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.FavoriteShopService;
import com.nailshop.nailborhood.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteShopService favoriteShopService;
    private final MemberService memberService;


    // 매장 찜
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/favorite/{shopId}")
    public ResponseEntity<ResultDto<FavoriteResponseDto>> getShopDetail(Authentication authentication,
                                                                        @AuthenticationPrincipal MemberDetails memberDetails,
                                                                        @PathVariable Long shopId){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        CommonResponseDto<Object> favoriteShop = favoriteShopService.favoriteShop(shopId, sessionDto.getId());
        ResultDto<FavoriteResponseDto> resultDto = ResultDto.in(favoriteShop.getStatus(), favoriteShop.getMessage());
        resultDto.setData((FavoriteResponseDto) favoriteShop.getData());

        return ResponseEntity.status(favoriteShop.getHttpStatus()).body(resultDto);
    }
}
