package com.nailshop.nailborhood.service.shop.admin;

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
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
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
public class ShopRequestLookupAdminService {
    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final ShopImgRepository shopImgRepository;

    // 매장 신청 리스트 조회

    @Transactional
    public CommonResponseDto<Object> getAllShopRequest(String keyword, int page, int size, String criteria, String sort) {

//        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort)
//                                                               .descending());

/*
        // 관리자 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
*/


        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, criteria)) : PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, criteria));
        // 페이지로 값 가져오기


        Page<Shop> shopRequestList;
        if (keyword == null || keyword.trim()
                                      .isEmpty()) {
            shopRequestList =  shopRepository.findAllNotDeletedAndReady(pageable);
        } else {
            shopRequestList = shopRepository.findAllNotDeletedAndReadyBySearch(keyword,pageable);
        }

        if (shopRequestList.isEmpty()) {
            throw new NotFoundException(ErrorCode.SHOP_REQUEST_NOT_FOUND);
        }

        // shop entity -> dto 변환
        Page<AllShopsLookupResponseDto> data = shopRequestList.map(shop -> {

            // shopImg imgNum =1 가져오기
            String shopMainImg = shopImgRepository.findByShopImgByShopIdAndShopImgId(shop.getShopId());

            long menuCnt = menuRepository.countByShopId(shop.getShopId());

            // dto에 shop entity 값을 변환하는 과정
            AllShopsLookupResponseDto dto = new AllShopsLookupResponseDto(
                    shop.getShopId(),
                    shopMainImg,
                    shop.getName(),
                    shop.getPhone(),
                    shop.getAddress(),
                    shop.getOpentime(),
                    shop.getWebsite(),
                    shop.getContent(),
                    shop.getStatus(),
                    shop.getIsDeleted(),
                    shop.getCreatedAt(),
                    shop.getReviewCnt(),
                    shop.getFavoriteCnt(),
                    shop.getRateAvg(),
                    menuCnt
            );
            // data 에 dto 반환
            return dto;
        });


        // 매장 리스트 가져오기
        List<AllShopsLookupResponseDto> allShopsLookupResponseDtoList = data.getContent();

        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(data.getTotalPages())
                                                   .totalElements(data.getTotalElements())
                                                   .pageNo(data.getNumber())
                                                   .isLastPage(data.isLast())
                                                   .build();

        // 페이지네이션을 포함한 매장 리스트 반환
        AllShopsListResponseDto allShopsListResponseDto = AllShopsListResponseDto.builder()
                                                                                 .allShopsLookupResponseDtoList(allShopsLookupResponseDtoList)
                                                                                 .paginationDto(paginationDto)
                                                                                 .build();

        return commonService.successResponse(SuccessCode.ALL_SHOP_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, allShopsListResponseDto);
    }

}