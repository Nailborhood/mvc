package com.nailshop.nailborhood.service.shop.admin;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopDeleteService {
    private final ShopRepository shopRepository;
    private final ShopImgRepository shopImgRepository;
    private final ArtRefRepository artRefRepository;
    private final ArtImgRepository artImgRepository;
    private final MenuRepository menuRepository;
    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final ArtLikeRepository artLikeRepository;
    private final CategoryArtRepository categoryArtRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

   @Transactional
    public CommonResponseDto<Object> deleteShop(String accessToken, Long shopId) {

       // 관리자 확인
       Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                      .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
       if (!admin.getRole().equals(Role.ROLE_ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        // 매장 존재 여부
       Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // menu 삭제
        menuRepository.deleteAllByShopId(shopId);


        // 아트판
        List<ArtRef> artRefList = artRefRepository.findAllByShopIdAndIsDeleted(shopId);

        if (!artRefList.isEmpty()) {
            for (ArtRef artRef : artRefList) {
                //  이미지 삭제
                List<ArtImg> artImgList = artImgRepository.findByArtRefId(artRef.getArtRefId());
                for (ArtImg artImg : artImgList) {
                    String artImgImgPath = artImg.getImgPath();
                    s3UploadService.deleteArtImg(artImgImgPath);
                    artImgRepository.deleteByArtImgId(artImg.getArtImgId(),true);
                }
                // 좋아요 수 0 및 ArtLike 삭제
                artRefRepository.makeLikeCountZero(artRef.getArtRefId());
                artLikeRepository.deleteByArtRefId(artRef.getArtRefId());

                // CategoryArt 삭제
                categoryArtRepository.deleteByArtRefArtRefId(artRef.getArtRefId());

                // ArtRef 삭제(isDeleted -> true)
                artRefRepository.deleteByArtRefId(artRef.getArtRefId(), true);
            }
        }




        // 매장
        // 매장 이미지 삭제
        List<ShopImg> shopImgList = shopImgRepository.findByShopImgListByShopId(shopId);
        for (ShopImg shopImg : shopImgList) {
            String shopImgImgPath = shopImg.getImgPath();
            s3UploadService.deleteShopImg(shopImgImgPath);
            shopImgRepository.deleteByShopImgId(shopImg.getShopImgId(),true);
        }

        // 매장 삭제
        //  shop -> isDeleted = true, shopStatus = closed
        ShopStatus shopStatus = ShopStatus.CLOSED;
        shopRepository.shopDeleteByShopId(shop.getShopId() ,shopStatus);

        return commonService.successResponse(SuccessCode.SHOP_DELETE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }


}
