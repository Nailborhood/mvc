package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Favorite;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.member.response.FavoriteResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.FavoriteRepository;
import com.nailshop.nailborhood.repository.member.MemeberFavoriteRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteShopService {
    private final TokenProvider tokenProvider;
    private final FavoriteRepository favoriteRepository;
    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final MemeberFavoriteRepository memeberFavoriteRepository;



    @Transactional
    public CommonResponseDto<Object> favoriteShop(Long shopId, String accessToken) {

        Long memberId = tokenProvider.getUserId(accessToken);

        Member member = memeberFavoriteRepository.findByMemberId(memberId);

        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 기존 매장 찜 존재
        Favorite existingFavorite = favoriteRepository.findByMemberIdAndShopId(memberId, shopId);

        // 기존 찜 정보가 없다면 새로운 찜을 생성, 그렇지 않다면 찜 상태를 변경
        if (existingFavorite == null) {
            return createFavorite(shopId, member, shop);
        } else {
            return toggleFavoriteStatus(existingFavorite, shopId);
        }
    }

    // 새로운 찜 생성 메서드
    private CommonResponseDto<Object> createFavorite(Long shopId, Member member, Shop shop) {
        // 새로운 찜을 생성
        Favorite favorite = Favorite.builder()
                                    .shop(shop)
                                    .member(member)
                                    .status(true)
                                    .build();

        // 매장의 찜 카운트를 증가
        shopRepository.updateFavoriteCntIncreaseByShopId(shopId);

        // 찜 정보를 저장
        Favorite savefavorite = favoriteRepository.save(favorite);

        return createSuccessResponse(savefavorite.getStatus(), SuccessCode.FAVORITE_SAVE_SUCCESS);
    }

    // 기존 찜이 있다면, 상태 변경 메서드
    private CommonResponseDto<Object> toggleFavoriteStatus(Favorite existingFavorite, Long shopId) {
        Long favoriteId = existingFavorite.getFavoriteId();

        // 기존 찜 상태를 반전 (찜함 -> 찜하지 않음, 찜하지 않음 -> 찜함)
        boolean newStatus = !existingFavorite.getStatus();

        // 새로운 찜 상태에 따라 매장의 찜 카운트를 증가 또는 감소
        if (newStatus) {
            // 기존 찜 false -> newStatus(true)
            shopRepository.updateFavoriteCntIncreaseByShopId(shopId);
        } else {
            // 기존 찜 true -> newStatus(false)
            shopRepository.updateFavoriteCntDecreaseByShopId(shopId);
        }

        // 찜 상태를 업데이트하고 저장
        favoriteRepository.updateStatus(favoriteId, newStatus);
        SuccessCode successCode = newStatus ? SuccessCode.FAVORITE_SAVE_SUCCESS : SuccessCode.FAVORITE_CANCEL_SUCCESS;

        return createSuccessResponse(newStatus, successCode);
    }

    // 찜 상태에 따라 dto 반환
    private CommonResponseDto<Object> createSuccessResponse(boolean status, SuccessCode successCode) {
        FavoriteResponseDto favoriteResponseDto = FavoriteResponseDto.builder()
                                                                     .status(status)
                                                                     .build();

        return commonService.successResponse(successCode.getDescription(), HttpStatus.OK, favoriteResponseDto);
    }

}
