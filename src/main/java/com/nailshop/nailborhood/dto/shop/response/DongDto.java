package com.nailshop.nailborhood.dto.shop.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongDto {
    String DongName;
    Long dongId;
    Long districtsId;
}
