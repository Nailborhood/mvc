package com.nailshop.nailborhood.service.shop.admin;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.chat.Message;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.domain.shop.CertificateImg;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.chat.ChattingRoomRepository;
import com.nailshop.nailborhood.repository.chat.MessageRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
import com.nailshop.nailborhood.repository.review.ReviewReportRepository;
import com.nailshop.nailborhood.repository.shop.CertificateImgRepository;
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
    private final ReviewReportRepository reviewReportRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final MessageRepository messageRepository;
    private final OwnerRepository ownerRepository;
    private final CertificateImgRepository certificateImgRepository;

    @Transactional
    public CommonResponseDto<Object> deleteShop(Long shopId) {

        // 관리자 확인
/*       Member admin = memberRepository.findByMemberIdAndIsDeleted(tokenProvider.getUserId(accessToken))
                                      .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
       if (!admin.getRole().equals(Role.ADMIN)) throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);*/

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
                    artImgRepository.deleteByArtImgId(artImg.getArtImgId(), true);
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

        // 리뷰 신고 삭제
/*        List<ReviewReport> reportList = reviewReportRepository.findAllByShopId(shop.getShopId());

        if (!reportList.isEmpty()) {
            for (ReviewReport report : reportList) {
                reviewReportRepository.deleteById(report.getReportId());
            }
        }*/

        // 채팅 삭제
/*        List<ChattingRoom> chattingRoomList = chattingRoomRepository.findAllByShopId(shop.getShopId());

        if (!chattingRoomList.isEmpty()) {
            for (ChattingRoom chattingRoom : chattingRoomList) {

                chattingRoomRepository.deleteByChattingRoomId(chattingRoom.getRoomId(),true);
            }
        }*/

        // 매장
        // 매장 이미지 삭제
        List<ShopImg> shopImgList = shopImgRepository.findByShopImgListByShopId(shopId);
        for (ShopImg shopImg : shopImgList) {
            String shopImgImgPath = shopImg.getImgPath();
            s3UploadService.deleteShopImg(shopImgImgPath);
            shopImgRepository.deleteByShopImgId(shopImg.getShopImgId(), true);
        }

        // 매장 사업자 등록증 삭제
        CertificateImg certificateImg = certificateImgRepository.findByShopId(shop.getShopId());
        s3UploadService.deleteCertificateImg(certificateImg.getImgPath());
        certificateImgRepository.deleteByCertificateImgId(certificateImg.getCertificateImgId(),true);

        // role 변경
        // owner -> user 로 변경
        Owner owner = ownerRepository.findByShopId(shop.getShopId());
        Member member = memberRepository.findById(owner.getMember().getMemberId())
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        member.changeRole(Role.ROLE_USER);
        // Owner isApproved ->false 로 변경
        ownerRepository.deleteByOwnerId(owner.getOwnerId(),false);

        // 매장 삭제
        //  shop -> isDeleted = true, shopStatus = closed
        ShopStatus shopStatus = ShopStatus.CLOSED;
        shopRepository.shopDeleteByShopId(shop.getShopId(), shopStatus);




        return commonService.successResponse(SuccessCode.SHOP_DELETE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }


}
