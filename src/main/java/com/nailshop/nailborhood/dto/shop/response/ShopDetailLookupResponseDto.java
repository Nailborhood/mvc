package com.nailshop.nailborhood.dto.shop.response;

import com.nailshop.nailborhood.type.ShopStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class ShopDetailLookupResponseDto {
    private Long shopId;
    private String name;
    private String phone;
    private String address;
    private String opentime;
    private String website;
    private String content;
    private ShopStatus status;
    private LocalDateTime createdAt;
    private double rateAvg;


    public ShopDetailLookupResponseDto(Long shopId, String name, String phone, String address, String opentime, String website, String content, ShopStatus status, LocalDateTime createdAt, double rateAvg) {
        this.shopId = shopId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.opentime = opentime;
        this.website = website;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.rateAvg = rateAvg;

    }
}
