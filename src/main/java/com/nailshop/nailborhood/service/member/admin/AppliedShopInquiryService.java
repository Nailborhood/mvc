package com.nailshop.nailborhood.service.member.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsLookupResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppliedShopInquiryService {

    private final CommonService commonService;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;


    // 매장등록신청 전체 조회
    public CommonResponseDto<Object> inquiryAllAppliedShop(String accessToken, int page, int size, String sortBy) {

        // 관리자 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                          .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        // 페이지 설정 및 MemberList get
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy).descending());

        Page<Shop> shopPage = shopRepository.findByIsDeletedFalseAndStatus(ShopStatus.BEFORE_OPEN, pageable);
        if (shopPage.isEmpty()) throw new NotFoundException(ErrorCode.SHOP_NOT_FOUND);

        List<Shop> shopList = shopPage.getContent();
        List<AllShopsLookupResponseDto> shopInfoDtoList = new ArrayList<>();

        // ShopInfoDto build
        for (Shop shop : shopList){

            long menuCnt = menuRepository.countByShopId(shop.getShopId());

            AllShopsLookupResponseDto allShopsLookupResponseDto = AllShopsLookupResponseDto.builder()
                    .shopId(shop.getShopId())
                    .name(shop.getName())
                    .phone(shop.getPhone())
                    .isDeleted(shop.getIsDeleted())
                    .createdAt(shop.getCreatedAt())
                    .rateAvg(shop.getRateAvg())
                    .address(shop.getAddress())
                    .status(shop.getStatus())
                    .opentime(shop.getOpentime())
                    .website(shop.getWebsite())
                    .content(shop.getContent())
                    .reviewCnt(shop.getReviewCnt())
                    .favoriteCnt(shop.getFavoriteCnt())
                    .menuCnt(menuCnt)
                    .build();

            shopInfoDtoList.add(allShopsLookupResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(shopPage.getTotalPages())
                .pageNo(shopPage.getNumber())
                .totalElements(shopPage.getTotalElements())
                .isLastPage(shopPage.isLast())
                .build();

        AllShopsListResponseDto allShopsListResponseDto = AllShopsListResponseDto.builder()
                .allShopsLookupResponseDtoList(shopInfoDtoList)
                .paginationDto(paginationDto)
                .build();

        return commonService.successResponse(SuccessCode.ALL_SHOP_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, allShopsListResponseDto);
    }

    // 매장등록신청 상세 조회
}
