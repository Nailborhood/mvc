package com.nailshop.nailborhood.dto.shop.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreAddressSeparationDto {
    private String cityName;
    private String districtsName;
    private String dongName;
}
