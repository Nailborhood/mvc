package com.nailshop.nailborhood.service.shop.owner;

import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsLookupResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopRequestLookupService {

    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final ShopImgRepository shopImgRepository;

    // 매장 신청 리스트 조회

    @Transactional
    public CommonResponseDto<Object> getShopRequest() {

        //TODO: accessToken or session 연결 필요
        Long ownerId = 17L;
        Shop shopRequestList = shopRepository.findAllShopListByOwnerId(ownerId);


        if (shopRequestList == null) {
            throw new NotFoundException(ErrorCode.SHOP_REQUEST_NOT_FOUND);
        }

        String shopMainImg = shopImgRepository.findByShopImgByShopIdAndShopImgId(shopRequestList.getShopId());
        long menuCnt = menuRepository.countByShopId(shopRequestList.getShopId());
        AllShopsLookupResponseDto allShopsLookupResponseDto = AllShopsLookupResponseDto.builder()
                                                                                       .shopMainImgPath(shopMainImg)
                                                                                       .shopId(shopRequestList.getShopId())
                                                                                       .name(shopRequestList.getName())
                                                                                       .phone(shopRequestList.getPhone())
                                                                                       .isDeleted(shopRequestList.getIsDeleted())
                                                                                       .createdAt(shopRequestList.getCreatedAt())
                                                                                       .address(shopRequestList.getAddress())
                                                                                       .status(shopRequestList.getStatus())
                                                                                       .opentime(shopRequestList.getOpentime())
                                                                                       .website(shopRequestList.getWebsite())
                                                                                       .content(shopRequestList.getContent())
                                                                                       .menuCnt(menuCnt)
                                                                                       .build();




        return commonService.successResponse(SuccessCode.ALL_SHOP_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, allShopsLookupResponseDto);
    }


}
