package com.nailshop.nailborhood.service.shop.owner;

import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.shop.CertificateImg;
import com.nailshop.nailborhood.domain.shop.Menu;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.shop.request.ShopMenuDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.repository.shop.*;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopRegistrationService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final DongRepository dongRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final ShopImgRepository shopImgRepository;
    private final CertificateImgRepository certificateImgRepository;

    // 매장 등록
    public CommonResponseDto<Object> registerShop(List<MultipartFile> multipartFileList,List<MultipartFile> fileList, ShopRegistrationRequestDto shopRegistrationRequestDto) {

        // 동 엔티티 설정
        String dongName = shopRegistrationRequestDto.getStoreAddressSeparation()
                                                    .getDongName();
       Optional<Dong> optionalDong = dongRepository.findByName(dongName);
       if(optionalDong.isEmpty()){
           return commonService.errorResponse(ErrorCode.DONG_NOT_FOUND.getDescription(), HttpStatus.OK, null);
       }

        Dong dong =  optionalDong.get();
        // 매장 세부정보 등록
        Shop shop = Shop.builder()
                        .status(ShopStatus.BEFORE_OPEN)
                        .content(shopRegistrationRequestDto.getContent())
                        .name(shopRegistrationRequestDto.getName())
                        .website(shopRegistrationRequestDto.getWebsite())
                        .address(shopRegistrationRequestDto.getAddress())
                        .opentime(shopRegistrationRequestDto.getOpentime())
                        .phone(shopRegistrationRequestDto.getPhone())
                        .isDeleted(false)
                        .dong(dong)
                        .reviewCnt(0)
                        .favoriteCnt(0)
                        .rateAvg(0)
                        .build();

        shop = shopRepository.save(shop);

        // 매장 메뉴 등록
        saveShopMenu(shopRegistrationRequestDto.getShopMenuDtoList(), shop);

        // 매장 사진 등록
        saveShopImg(multipartFileList, shop);

        // 사업자 증명 사진 등록
        saveCertificateImg(fileList, shop);
        return commonService.successResponse(SuccessCode.SHOP_REGISTRATION_SUCCESS.getDescription(), HttpStatus.OK, null);
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

    // 매장 이미지 저장
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

    // 사업자 증명 사진 저장
    private void saveCertificateImg(List<MultipartFile> fileList, Shop shop) {
        // s3에 이미지 업로드
        List<String> certificateImgUrlList = s3UploadService.certificateImgUpload(fileList);

        // 이미지 번호 1번 부터 시작
        Integer imgNum = 1;

        for (String imgPath : certificateImgUrlList) {
            CertificateImg certificateImg = CertificateImg.builder()
                                            .imgPath(imgPath)
                                            .imgNum(imgNum)
                                            .isDeleted(false)
                                            .shop(shop)
                                            .build();
            certificateImgRepository.save(certificateImg);

            imgNum++;
        }
    }

}
