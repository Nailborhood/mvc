package com.nailshop.nailborhood.dto.shop.response;

import com.nailshop.nailborhood.domain.address.City;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StoreAddressSeparationListDto {
    List<CityDto> cityDtoList;
    List<DistrictsDto> districtsDtoList;
    List<DongDto> dongDtoList;
}
