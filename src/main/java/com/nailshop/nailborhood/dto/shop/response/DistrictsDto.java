package com.nailshop.nailborhood.dto.shop.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DistrictsDto {
    String districtsName;
    Long districtsId;
    Long cityId;
}
