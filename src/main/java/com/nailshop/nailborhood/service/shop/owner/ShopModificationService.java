package com.nailshop.nailborhood.service.shop.owner;

import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.shop.Menu;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.shop.request.ShopMenuDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.shop.DongRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopModificationService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final DongRepository dongRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final ShopImgRepository shopImgRepository;

    @Transactional
    public CommonResponseDto<Object> updateShop(Long shopId, List<MultipartFile> multipartFileList, ShopModifiactionRequestDto shopModifiactionRequestDto) {

        // 매장 아이디 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
        // 주소(동) 수정
        String dongName = shopModifiactionRequestDto.getStoreAdressSeparation()
                                                    .getDongName();
        Dong dong = dongRepository.findByName(dongName)
                                  .get();


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
                shopModifiactionRequestDto.getAddress(),
                shopModifiactionRequestDto.getOpentime(),
                shopModifiactionRequestDto.getWebsite(),
                shopModifiactionRequestDto.getContent(),
                ShopStatus.valueOf(String.valueOf(shopModifiactionRequestDto.getStatus())),
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
    private void saveShopImg(List<MultipartFile> multipartFileList, Shop shop){
        // s3에 이미지 업로드
        List<String> shopImgUrlList = s3UploadService.shopImgUpload(multipartFileList);

        // 이미지 번호 1번 부터 시작
        Integer imgNum = 1;

        for(String imgPath : shopImgUrlList){
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
