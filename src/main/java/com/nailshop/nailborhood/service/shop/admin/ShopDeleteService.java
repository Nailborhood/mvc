package com.nailshop.nailborhood.service.shop.admin;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Favorite;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.shop.Menu;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.example.ExampleDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.member.FavoriteRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.member.MemeberFavoriteRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.artboard.ArtDeleteService;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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

   @Transactional
    public CommonResponseDto<Object> deleteShop(Long shopId) {

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // menu 삭제
        menuRepository.deleteAllByShopId(shopId);

        // 리뷰
/*        List<Review> reviewList = reviewRepository.findAllByShopIdAndIsDeleted(shopId);


        if (!reviewList.isEmpty()) {
            for (Review review : reviewList) {
                // 리뷰 이미지 삭제
                List<ReviewImg> reviewImgList = reviewImgRepository.findAllByReviewId(review.getReviewId());
                for (ReviewImg reviewImg : reviewImgList) {
                    String reviewImgImgPath = reviewImg.getImgPath();
                    s3UploadService.deleteReviewImg(reviewImgImgPath);
                    reviewImgRepository.deleteAllByReviewImgId(reviewImg.getReviewImgId());
                }

                // 리뷰 삭제 ( 리뷰 별점 0 , 리뷰 isDeleted =true )
                reviewRepository.reviewDeleteByReviewId(review.getReviewId());
            }
        }*/

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


        // 매장 찜
/*        List<Favorite> favoriteList = favoriteRepository.findAllByShopIdAndIsDeleted(shopId);

        if (!favoriteList.isEmpty()) {
            for (Favorite favorite : favoriteList) {
                // 매장 찜 삭제
                favoriteRepository.updateStatus(favorite.getFavoriteId(), false);
            }
        }*/

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
