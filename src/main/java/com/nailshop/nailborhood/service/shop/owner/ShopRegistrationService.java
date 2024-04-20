package com.nailshop.nailborhood.service.shop.owner;

import com.nailshop.nailborhood.domain.address.City;
import com.nailshop.nailborhood.domain.address.Districts;
import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.shop.CertificateImg;
import com.nailshop.nailborhood.domain.shop.Menu;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.shop.request.ShopMenuDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.request.StoreAddressSeparationDto;
import com.nailshop.nailborhood.dto.shop.response.CityDto;
import com.nailshop.nailborhood.dto.shop.response.DistrictsDto;
import com.nailshop.nailborhood.dto.shop.response.DongDto;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.address.CityRepository;
import com.nailshop.nailborhood.repository.address.DistrictsRepository;
import com.nailshop.nailborhood.repository.address.DongRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
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
    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;
    private final CityRepository cityRepository;
    private final DistrictsRepository districtsRepository;


    // 매장 등록
    public CommonResponseDto<Object> registerShop(Member member, List<MultipartFile> multipartFileList, List<MultipartFile> fileList, ShopRegistrationRequestDto shopRegistrationRequestDto) {



        Long memberId = member.getMemberId();
        memberRepository.findById(memberId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 이미 owner ( 신청을 이미 한 사람 ) 은 더이상 매장 신청 할 수 없게 처리
        Owner existingOwner = ownerRepository.findByMemberId(memberId);

        if (existingOwner != null) {
            throw new NotFoundException(ErrorCode.OWNER_ALREADY_EXIST);
        }


        // 주소
        Long cityId = shopRegistrationRequestDto.getStoreAddressSeparationDto()
                                                .getCityId();
        Long districtsId = shopRegistrationRequestDto.getStoreAddressSeparationDto()
                                                     .getDistrictsId();
        Long dongId = shopRegistrationRequestDto.getStoreAddressSeparationDto()
                                                    .getDongId();

        City city = cityRepository.findByCityId(cityId);
        Districts districts = districtsRepository.findByDistrictsId(districtsId);
        Dong dong = dongRepository.findByDongId(dongId);


/*        Optional<Dong> optionalDong = dongRepository.findByName(dongName);
        if (optionalDong.isEmpty()) {
            return commonService.errorResponse(ErrorCode.DONG_NOT_FOUND.getDescription(), HttpStatus.OK, null);
        }

        Dong dong = optionalDong.get();
*/

        // 매장 세부정보 등록
        Shop shop = Shop.builder()
                        .status(ShopStatus.READY)
                        .content(shopRegistrationRequestDto.getContent())
                        .name(shopRegistrationRequestDto.getName())
                        .website(shopRegistrationRequestDto.getWebsite())
                        .address(String.join(" ", city.getName(), districts.getName(), dong.getName(), shopRegistrationRequestDto.getAddress()))
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

        // Owner 등록
        Owner owner = Owner.builder()
                           .isApproved(false)
                           .shop(shop)
                           .member(member)
                           .build();

        ownerRepository.save(owner);


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

    // DB에 저장되어 있는 주소 가져오기
    public StoreAddressSeparationListDto findAddress() {
        List<City> cityList = cityRepository.findAll();
        List<CityDto> cityDtoList = new ArrayList<>();

        for (City city : cityList) {
            cityDtoList.add(CityDto.builder()
                                   .cityName(city.getName())
                                   .cityId(city.getCityId())
                                   .build());
        }
        List<Districts> districtsList = districtsRepository.findAll();
        List<DistrictsDto> districtsDtoList = new ArrayList<>();

        for (Districts districts : districtsList) {
            districtsDtoList.add(DistrictsDto.builder()
                                             .districtsName(districts.getName())
                                             .districtsId(districts.getDistrictsId())
                                             .cityId(districts.getCity()
                                                              .getCityId())
                                             .build());
        }

        List<Dong> dongList = dongRepository.findAll();
        List<DongDto> dongDtoList = new ArrayList<>();

        for (Dong dong : dongList) {
            dongDtoList.add(DongDto.builder()
                                   .dongName(dong.getName())
                                   .dongId(dong.getDongId())
                                   .districtsId(dong.getDistricts()
                                                    .getDistrictsId())
                                   .build());
        }

        StoreAddressSeparationListDto storeAddressSeparationListDto = StoreAddressSeparationListDto.builder()
                                                                                                   .cityDtoList(cityDtoList)
                                                                                                   .districtsDtoList(districtsDtoList)
                                                                                                   .dongDtoList(dongDtoList)
                                                                                                   .build();

        return storeAddressSeparationListDto;
    }

    public void updateAddressInfo(ShopRegistrationRequestDto shopRegistrationRequestDto, StoreAddressSeparationDto storeAddressSeparationDto) {
/*        StoreAddressSeparationDto storeAddress = StoreAddressSeparationDto.builder()
                                                                          .cityName(storeAddressSeparationDto.getCityName())
                                                                          .districtsName(storeAddressSeparationDto.getDistrictsName())
                                                                          .dongName(storeAddressSeparationDto.getDongName())
                                                                          .build();*/

//        ShopRegistrationRequestDto registrationRequestDto = ShopRegistrationRequestDto.builder()
//                                                                                      .storeAddressSeparationDto(storeAddress)
//                                                                                      .build();
        shopRegistrationRequestDto.setStoreAddressSeparationDto(storeAddressSeparationDto);
    }


   public boolean checkExistingOwner(Member member) {

        Member memberInfo = memberRepository.findById(member.getMemberId())
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 이미 owner ( 신청을 이미 한 사람 ) 은 더이상 매장 신청 할 수 없게 처리
        Owner existingOwner = ownerRepository.findByMemberId(memberInfo.getMemberId());


        if (existingOwner != null) {
            // 이미 신청한 상황
            return false;
        } else {
            // 매장 신청이 없는 상황
            return true;
        }
    }
}
