package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.category.CategoryArt;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.ArtRegistrationRequestDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtRegistrationService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final ArtImgRepository artImgRepository;
    private final ArtRefRepository artRefRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryArtRepository categoryArtRepository;

    @Transactional
    public CommonResponseDto<Object> registerArt(List<MultipartFile> multipartFileList, ArtRegistrationRequestDto artRegistrationRequestDto) {

        // shop 정보 get
        Shop shop = shopRepository.findById(artRegistrationRequestDto.getShopId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // ArtRef 저장
        ArtRef artRef = ArtRef.builder()
                .name(artRegistrationRequestDto.getName())
                .content(artRegistrationRequestDto.getContent())
                .likeCount(0L)
                .isDeleted(false)
                .shop(shop)
                .build();

        artRef = artRefRepository.save(artRef);

        // ArtImg 저장
        saveArtImg(multipartFileList, artRef);

        // CategoryArt 저장
        for (Long categoryId : artRegistrationRequestDto.getCategoryIdList()){

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

            CategoryArt categoryArt = CategoryArt.builder()
                    .artRef(artRef)
                    .category(category)
                    .build();

            categoryArtRepository.save(categoryArt);
        }

        return commonService.successResponse(SuccessCode.ART_REGISTRATION_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    // 이미지 저장
    private void saveArtImg(List<MultipartFile> multipartFileList, ArtRef artRef){

        List<String> artImgUrlList = s3UploadService.artImgUpload(multipartFileList);

        int imgNum = 1;

        for(String imgPath : artImgUrlList){
            ArtImg artImg = ArtImg.builder()
                    .imgPath(imgPath)
                    .imgNum(imgNum)
                    .isDeleted(false)
                    .artRef(artRef)
                    .build();

            artImgRepository.save(artImg);
            imgNum++;
        }
    }
}
