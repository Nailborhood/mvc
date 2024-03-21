package com.nailshop.nailborhood.dto.shop.request;

import com.nailshop.nailborhood.type.ShopStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShopModifiactionRequestDto {

    private Long shopId;
    private String name;
    private String phone;
    private String address;
    private String opentime;
    private String website;
    private String content;
    private ShopStatus status;
    private StoreAddressSeparationDto storeAddressSeparationDto;
    private List<ShopMenuDto> shopMenuDtoList;

}
