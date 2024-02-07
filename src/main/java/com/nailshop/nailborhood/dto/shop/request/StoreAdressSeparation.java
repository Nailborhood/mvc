package com.nailshop.nailborhood.dto.shop.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreAdressSeparation {
    private String cityName;
    private String districtsName;
    private String dongName;
}
