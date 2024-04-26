package com.nailshop.nailborhood.service.mypage;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.mypage.FavoriteShopDetailDto;
import com.nailshop.nailborhood.dto.mypage.MyFavoriteListResponseDto;
import com.nailshop.nailborhood.dto.mypage.MyReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.member.FavoriteRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
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
public class MypageService {

    private final CommonService commonService;
    private final ReviewRepository reviewRepository;
    private final CategoryReviewRepository categoryReviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    // 내가 쓴 리뷰 조회 (마이페이지)
    public CommonResponseDto<Object> myReview(Long memberId, int page, int size, String sortBy) {

//        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
//                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

//        if (!member.getRole().equals(Role.ROLE_USER)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy).descending());


//        Page<Review> myReviewPage = reviewRepository.findMyReviewListByMemberId(memberId, pageable, "신고 처리됨");
        Page<Review> myReviewPage = reviewRepository.findMyReviewListByMemberId(memberId, pageable);

        if (myReviewPage.isEmpty()) throw new NotFoundException(ErrorCode.REVIEW_NOT_REGISTRATION);

        List<Review> myReviewList = myReviewPage.getContent();
        List<ReviewResponseDto> myReviewResponseDtoList = new ArrayList<>();

        for(Review review : myReviewList ){

            String mainImgPath = review.getReviewImgList().get(0).getImgPath();
            String shopName = review.getShop().getName();

            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .shopId(review.getShop().getShopId())
                    .mainImgPath(mainImgPath)
                    .categoryTypeList(categoryTypeList)
                    .shopName(shopName)
                    .shopAddress(review.getShop().getAddress())
                    .contents(review.getContents())
                    .rate(review.getRate())
                    .likeCnt(review.getLikeCnt())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();

            myReviewResponseDtoList.add(reviewResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(myReviewPage.getTotalPages())
                .totalElements(myReviewPage.getTotalElements())
                .pageNo(myReviewPage.getNumber())
                .isLastPage(myReviewPage.isLast())
                .build();

        MyReviewListResponseDto myReviewListResponseDto = MyReviewListResponseDto.builder()
                .reviewResponseDtoList(myReviewResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.MY_REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, myReviewListResponseDto);
    }

    // 찜한 매장 조회
    public CommonResponseDto<Object> myFavorite(Long memberId,int page, int size) {

        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.getRole().equals(Role.ROLE_USER)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
        PageRequest pageable = PageRequest.of(page - 1, size);

        Page<Shop> myFavoritePage = favoriteRepository.findFavoriteListByMemberId(memberId, pageable);
        if (myFavoritePage.isEmpty()) throw new NotFoundException(ErrorCode.SHOP_FAVORITE_EMPTY);

        List<Shop> favoriteShopList = myFavoritePage.getContent();
        List<FavoriteShopDetailDto> myFavoriteResponseDtoList = new ArrayList<>();

        for (Shop shop : favoriteShopList) {

            String mainImgPath = shop.getShopImgList().get(0).getImgPath();

            FavoriteShopDetailDto favoriteShopDetailDto = FavoriteShopDetailDto.builder()
                    .shopId(shop.getShopId())
                    .shopName(shop.getName())
                    .mainImgPath(mainImgPath)
                    .favoriteCnt(shop.getFavoriteCnt())
                    .isDeleted(shop.getIsDeleted())
                    .rateAvg(shop.getRateAvg())
                    .reviewCnt(shop.getReviewCnt())
                    .address(shop.getAddress())
                    .build();

            myFavoriteResponseDtoList.add(favoriteShopDetailDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(myFavoritePage.getTotalPages())
                .totalElements(myFavoritePage.getTotalElements())
                .pageNo(myFavoritePage.getNumber())
                .isLastPage(myFavoritePage.isLast())
                .build();

        MyFavoriteListResponseDto myFavoriteListResponseDto = MyFavoriteListResponseDto.builder()
                .favoriteShopDetailDtoList(myFavoriteResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.MY_FAVORITE_SHOP_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, myFavoriteListResponseDto);
    }

    // 북마크한 아트 조회
    public CommonResponseDto<Object> myBookmark(Long memberId,int page, int size) {

        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.getRole().equals(Role.ROLE_USER)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
        PageRequest pageable = PageRequest.of(page - 1, size);

        Page<ArtRef> myBookmarkPage;
        if (myBookmarkPage.isEmpty()) throw new NotFoundException(ErrorCode.ART_BOOKMARK_EMPTY);

        List<ArtRef> myBookmarkList = myBookmarkPage.getContent();
        List<ArtResponseDto> artResponseDtoList = new ArrayList<>();

        for (ArtRef artRef : myBookmarkList) {

            String mainImgPath = artRef.getArtImgList()
                    .getFirst()
                    .getImgPath();
            String shopName = artRef.getShop()
                    .getName();

            ArtResponseDto artResponseDto = ArtResponseDto.builder()
                    .id(artRef.getArtRefId())
                    .name(artRef.getName())
                    .content(artRef.getContent())
                    .likeCount(artRef.getLikeCount())
                    .mainImgPath(mainImgPath)
                    .shopName(shopName)
                    .createdAt(artRef.getCreatedAt())
                    .updatedAt(artRef.getUpdatedAt())
                    .build();

            artResponseDtoList.add(artResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(myBookmarkPage.getTotalPages())
                .totalElements(myBookmarkPage.getTotalElements())
                .pageNo(myBookmarkPage.getNumber())
                .isLastPage(myBookmarkPage.isLast())
                .build();

        ArtListResponseDto artListResponseDto = ArtListResponseDto.builder()
                .artResponseDtoList(artResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.MY_FAVORITE_SHOP_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, artListResponseDto);
    }


}
