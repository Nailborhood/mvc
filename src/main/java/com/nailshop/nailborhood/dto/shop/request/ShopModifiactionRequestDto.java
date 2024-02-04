package com.nailshop.nailborhood.dto.shop.request;

import com.nailshop.nailborhood.type.ShopStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopModifiactionRequestDto {

    private String name;
    private String phone;
    private String address;
    private String opentime;
    private String website;
    private String content;
    private ShopStatus status;
    private StoreAdressSeparation storeAdressSeparation;
    private List<ShopMenuDto> shopMenuDtoList;

}
