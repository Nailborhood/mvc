package com.nailshop.nailborhood.dto.shop.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongDto {
    String dongName;
    Long dongId;
    Long districtsId;
}
