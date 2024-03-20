package com.nailshop.nailborhood.service.shop.owner;

import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.shop.Menu;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.shop.request.ShopMenuDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.address.DongRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopModificationService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final DongRepository dongRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final ShopImgRepository shopImgRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final OwnerRepository ownerRepository;

    //TODO: user 연결 필요
    @Transactional
    public CommonResponseDto<Object> updateShop(Long shopId, List<MultipartFile> multipartFileList, ShopModifiactionRequestDto shopModifiactionRequestDto) {
        Long memberId = 2L;

        // 매장 Owner 인지 확인
        Member owner = memberRepository.findByMemberIdAndIsDeleted(memberId)
                                       .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Owner shopOwner = ownerRepository.findByMemberId(owner.getMemberId());
        if (!owner.getRole()
                  .equals(Role.OWNER) && shopOwner.getShop()
                                                  .getShopId()
                                                  .equals(shopId))
            throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);

        // 매장 아이디 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
        // 주소(동) 수정
        String city = shopModifiactionRequestDto.getStoreAddressSeparationDto()
                                                .getCityName();
        String districts = shopModifiactionRequestDto.getStoreAddressSeparationDto()
                                                     .getDistrictsName();
        String dongName = shopModifiactionRequestDto.getStoreAddressSeparationDto()
                                                    .getDongName();

        Optional<Dong> optionalDong = dongRepository.findByName(dongName);
        if (optionalDong.isEmpty()) {
            return commonService.errorResponse(ErrorCode.DONG_NOT_FOUND.getDescription(), HttpStatus.OK, null);
        }

        Dong dong = optionalDong.get();


        // 기존 메뉴 삭제
        menuRepository.deleteAllByShopId(shopId);

        //새로운 메뉴 정보 저장
        saveShopMenu(shopModifiactionRequestDto.getShopMenuDtoList(), shop);

        // 기존 이미지 삭제
        updateShopImg(multipartFileList, shop);

        // 새로운 이미지 저장
        saveShopImg(multipartFileList, shop);

        // 매장 정보 저장
       shop.shopUpdate(shopModifiactionRequestDto.getName(),
               String.join(" ", city, districts, dongName, shopModifiactionRequestDto.getAddress()),
                shopModifiactionRequestDto.getOpentime(),
                shopModifiactionRequestDto.getWebsite(),
                shopModifiactionRequestDto.getContent(),
                dong,
                shopModifiactionRequestDto.getPhone());


        shopRepository.save(shop);


        return commonService.successResponse(SuccessCode.SHOP_MODIFICATION_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    // 매장 메뉴 등록 메서드
    private void saveShopMenu(List<ShopMenuDto> shopMenuDtoList, Shop shop) {
        List<Menu> menuList = new ArrayList<>();

        for (ShopMenuDto shopMenuDto : shopMenuDtoList) {
            Menu menu = Menu.builder()
                            .name(shopMenuDto.getName())
                            .price(shopMenuDto.getPrice())
                            .shop(shop)
                            .build();
            menuList.add(menu);
        }

        menuRepository.saveAll(menuList);

    }

    // 매장 기존 이미지 삭제
    private void updateShopImg(List<MultipartFile> multipartFileList, Shop shop) {
        Long shopId = shop.getShopId();

        if (multipartFileList != null) {
            // url 값 삭제
            List<ShopImg> shopImgList = shopImgRepository.findByShopImgListByShopId(shopId);
            for (ShopImg shopImg : shopImgList) {
                String shopImgUrl = shopImg.getImgPath();
                // s3 이미지 삭제
                s3UploadService.deleteShopImg(shopImgUrl);
            }

            // DB url 삭제
            shopImgRepository.deleteByShopId(shopId);

        }
    }

    // 이미지 저장
    private void saveShopImg(List<MultipartFile> multipartFileList, Shop shop) {
        // s3에 이미지 업로드
        List<String> shopImgUrlList = s3UploadService.shopImgUpload(multipartFileList);

        // 이미지 번호 1번 부터 시작
        Integer imgNum = 1;

        for (String imgPath : shopImgUrlList) {
            ShopImg shopImg = ShopImg.builder()
                                     .imgPath(imgPath)
                                     .imgNum(imgNum)
                                     .isDeleted(false)
                                     .shop(shop)
                                     .build();
            shopImgRepository.save(shopImg);

            imgNum++;
        }
    }




}
