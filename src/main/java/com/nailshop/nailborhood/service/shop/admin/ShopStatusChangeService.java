package com.nailshop.nailborhood.service.shop.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopStatusChangeService {
    private final ShopRepository shopRepository;
    private final CommonService commonService;
    private final MemberRepository memberRepository;


    @Transactional
    // 매장 상태 변경
    public CommonResponseDto<Object> changeShopStatus(String accessToken, Long shopId, String status) {

        // 관리자 확인
        /*Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ROLE_ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);*/

        ShopStatus changeStatus = null;
        SuccessCode successCode = null;


        if (status.equals("before")) {
            // 매장 오픈 전
            changeStatus = ShopStatus.BEFORE_OPEN;
            successCode = SuccessCode.SHOP_STATUS_BEFORE_OPEN;
        } else if (status.equals("closed")) {
            // 매장 종료
            changeStatus = ShopStatus.CLOSED;
            successCode = SuccessCode.SHOP_STATUS_CLOSED;
        } else {
            // 매장 오픈
            changeStatus = ShopStatus.OPEN;
            successCode = SuccessCode.SHOP_STATUS_OPEN;
        }

        if (changeStatus == null) {
            throw new BadRequestException(ErrorCode.SHOPSTATUS_NOT_FOUND);
        }
        shopRepository.updateShopStatusByShopId(shopId, changeStatus);

        return commonService.successResponse(successCode.getDescription(), HttpStatus.OK, null);
    }
}


