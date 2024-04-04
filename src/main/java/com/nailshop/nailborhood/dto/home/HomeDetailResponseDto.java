package com.nailshop.nailborhood.dto.home;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class HomeDetailResponseDto {

    List<ResultDto<ArtListResponseDto>> artListResponseDtoList;
    List<ResultDto<ShopListResponseDto>> shopListByReviewResponseDtoList;
    List<ResultDto<ShopListResponseDto>> shopListByRateResponseDtoList;
}
