package com.nailshop.nailborhood.dto.shop.response.detail;

import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.CityDto;
import com.nailshop.nailborhood.dto.shop.response.DistrictsDto;
import com.nailshop.nailborhood.dto.shop.response.DongDto;
import com.nailshop.nailborhood.dto.shop.response.ShopAndReviewLookUpResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class MyShopDetailListResponseDto {
    private ShopDetailLookupResponseDto shopDetailLookupResponseDto;
    private List<ShopImgListResponseDto> shopImgListResponseDtoList;
    private List<MenuDetailResponseDto> menuDetailResponseDtoList;
    private CityDto cityDto;
    private DistrictsDto districtsDto;
    private DongDto dongDto;


}
