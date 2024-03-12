package com.nailshop.nailborhood.service.member.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
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
public class ShopRegistrationHandler {

    private final CommonService commonService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final OwnerRepository ownerRepository;

    @Transactional
    public CommonResponseDto<Object> shopApprove(String accessToken, Long shopId) {

        // 권한 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        // shop, owner 정보 get
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        Owner owner = shop.getOwner();
        Member member = owner.getMember();

        // member role 및 isApproved 변경
        member.changeRole(Role.OWNER);
        owner.changeIsApproved(true);

        // shop status 변경
        shop.changeStatus(ShopStatus.OPEN);

        return commonService.successResponse(SuccessCode.APPROVE_SHOP_REGISTRATION.getDescription(), HttpStatus.OK, null);
    }

    @Transactional
    public CommonResponseDto<Object> shopReject(String accessToken, Long shopId){

        // 권한 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        // shop, owner 정보 get
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        Owner owner = shop.getOwner();

        // owner 삭제
        ownerRepository.deleteById(owner.getOwnerId());

        // shop 삭제
        shopRepository.deleteById(shopId);

        return commonService.successResponse(SuccessCode.REJECT_SHOP_REGISTRATION.getDescription(), HttpStatus.OK, null);
    }
}
