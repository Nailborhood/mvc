package com.nailshop.nailborhood.service.search;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import com.nailshop.nailborhood.dto.review.response.admin.AdminReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.admin.AdminReviewResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsLookupResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.member.CustomerRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSearchService {

    private final CommonService commonService;
    private final ReviewRepository reviewRepository;
    private final ArtRefRepository artRefRepository;
    private final ShopRepository shopRepository;
    private final CategoryReviewRepository categoryReviewRepository;
    private final CategoryArtRepository categoryArtRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final ShopImgRepository shopImgRepository;
    private final MenuRepository menuRepository;
    private final CustomerRepository customerRepository;

    // 리뷰로 검색
    public CommonResponseDto<Object> searchReviewInquiry(String keyword, int page, int size, String sortBy) {


        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());

        Page<Review> reviewSearchPage;
        if (keyword == null || keyword.trim()
                                      .isEmpty()) {
            reviewSearchPage = reviewRepository.findAllIsDeletedFalse(pageable);
        } else {
            reviewSearchPage = reviewRepository.findAllReviewListBySearch(keyword, pageable);
        }

        if (reviewSearchPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }


        List<Review> reviewList = reviewSearchPage.getContent();
        List<AdminReviewResponseDto> adminReviewResponseDtoList = new ArrayList<>();

        for (Review review : reviewList) {

            String mainImgPath = review.getReviewImgList()
                                       .get(0)
                                       .getImgPath();
            String shopName = review.getShop()
                                    .getName();


            String reviewer = customerRepository.findByMemberId(review.getCustomer()
                                                                      .getMember()
                                                                      .getMemberId())
                                                .getMember()
                                                .getName();


            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            AdminReviewResponseDto adminReviewResponseDto = AdminReviewResponseDto.builder()
                                                                             .reviewId(review.getReviewId())
                                                                             .shopName(shopName)
                                                                             .mainImgPath(mainImgPath)
                                                                             .categoryTypeList(categoryTypeList)
                                                                             .contents(review.getContents())
                                                                             .rate(review.getRate())
                                                                             .likeCnt(review.getLikeCnt())
                                                                             .createdAt(review.getCreatedAt())
                                                                             .updatedAt(review.getUpdatedAt())
                                                                             .reviewer(reviewer)
                                                                             .isDeleted(review.isDeleted())
                                                                             .build();

            adminReviewResponseDtoList.add(adminReviewResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(reviewSearchPage.getTotalPages())
                                                   .totalElements(reviewSearchPage.getTotalElements())
                                                   .pageNo(reviewSearchPage.getNumber())
                                                   .isLastPage(reviewSearchPage.isLast())
                                                   .build();

        AdminReviewListResponseDto adminReviewListResponseDto = AdminReviewListResponseDto.builder()
                                                                                          .adminReviewResponseDtoList(adminReviewResponseDtoList)
                                                                                          .paginationDto(paginationDto)
                                                                                          .build();

        return commonService.successResponse(SuccessCode.SEARCH_BY_REVIEW_SUCCESS.getDescription(), HttpStatus.OK, adminReviewListResponseDto);
    }

    // 아트판으로 검색
    public CommonResponseDto<Object> searchArtRefInquiry(String accessToken, String keyword, int page, int size, String sortBy) {
        // 관리자 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole()
                  .equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());

        Page<ArtRef> artSearchPage = artRefRepository.findAllArtRefListBySearch(keyword, pageable);
        if (artSearchPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.ART_NOT_FOUND);
        }

        List<ArtRef> artRefList = artSearchPage.getContent();
        List<ArtResponseDto> artResponseDtoList = new ArrayList<>();

        for (ArtRef artRef : artRefList) {

            String mainImgPath = artRef.getArtImgList()
                                       .get(0)
                                       .getImgPath();
            String shopName = artRef.getShop()
                                    .getName();

            List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRef.getArtRefId());

            ArtResponseDto artResponseDto = ArtResponseDto.builder()
                                                          .name(artRef.getName())
                                                          .content(artRef.getContent())
                                                          .likeCount(artRef.getLikeCount())
                                                          .mainImgPath(mainImgPath)
                                                          .shopName(shopName)
                                                          .categoryTypeList(categoryTypeList)
                                                          .createdAt(artRef.getCreatedAt())
                                                          .updatedAt(artRef.getUpdatedAt())
                                                          .build();

            artResponseDtoList.add(artResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(artSearchPage.getTotalPages())
                                                   .totalElements(artSearchPage.getTotalElements())
                                                   .pageNo(artSearchPage.getNumber())
                                                   .isLastPage(artSearchPage.isLast())
                                                   .build();

        ArtListResponseDto artListResponseDto = ArtListResponseDto.builder()
                                                                  .artResponseDtoList(artResponseDtoList)
                                                                  .paginationDto(paginationDto)
                                                                  .build();

        return commonService.successResponse(SuccessCode.SEARCH_BY_ART_SUCCESS.getDescription(), HttpStatus.OK, artListResponseDto);
    }

    // 매장 검색
    public CommonResponseDto<Object> searchShopInquiry(String keyword, int page, int size, String sortBy) {

        // 관리자 확인
//        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
//                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
//        if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());
        Page<Shop> shopSearchPage;
        if (keyword == null || keyword.trim()
                                      .isEmpty()) {
            shopSearchPage = shopRepository.findAllNotDeleted(pageable);
        } else {
            shopSearchPage = shopRepository.findALlShopListByKeyword(keyword, pageable);
        }

        if (shopSearchPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.SHOP_NOT_FOUND);
        }

        // shop entity -> dto 변환
        Page<AllShopsLookupResponseDto> data = shopSearchPage.map(shop -> {

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
        return commonService.successResponse(SuccessCode.SEARCH_BY_SHOP_SUCCESS.getDescription(), HttpStatus.OK, allShopsListResponseDto);
    }


    // 회원 검색

    public CommonResponseDto<Object> searchMemberInquiry(String accessToken, String keyword, int page, int size, String sortBy) {

        // 관리자 확인
        Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!admin.getRole()
                  .equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        // 페이지 설정 및 MemberList get
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());

        Page<Member> memberPage = memberRepository.findAllMemberListByKeyword(keyword, pageable);
        if (memberPage.isEmpty()) throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);

        List<Member> memberList = memberPage.getContent();
        List<MemberInfoDto> memberInfoDtoList = new ArrayList<>();

        // MemberInfoDto build
        for (Member member : memberList) {

            MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                                                       .email(member.getEmail())
                                                       .name(member.getName())
                                                       .birthday(member.getBirthday())
                                                       .phoneNum(member.getPhoneNum())
                                                       .gender(member.getGender())
                                                       .address(member.getAddress())
                                                       .nickname(member.getNickname())
                                                       .profileImg(member.getProfileImg())
                                                       .isDeleted(member.isDeleted())
                                                       .createdAt(member.getCreatedAt())
                                                       .build();

            memberInfoDtoList.add(memberInfoDto);
        }

        // PaginationDto build
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(memberPage.getTotalPages())
                                                   .totalElements(memberPage.getTotalElements())
                                                   .pageNo(memberPage.getNumber())
                                                   .isLastPage(memberPage.isLast())
                                                   .build();

        // MemberListResponseDto build
        MemberListResponseDto memberListResponseDto = MemberListResponseDto.builder()
                                                                           .memberInfoDtoList(memberInfoDtoList)
                                                                           .paginationDto(paginationDto)
                                                                           .build();

        return commonService.successResponse(SuccessCode.MEMBER_ALL_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, memberListResponseDto);
    }


}
