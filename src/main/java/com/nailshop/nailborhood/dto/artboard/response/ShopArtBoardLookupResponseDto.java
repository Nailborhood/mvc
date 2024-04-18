package com.nailshop.nailborhood.dto.artboard.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ShopArtBoardLookupResponseDto {

    private Long artRefId;
    private Long shopId;
    private String name;
    private String content;
    private String artImgPath;
    private LocalDateTime createdAt;
    private List<Long> categoryIdList;
    private List<String> categoryTypeList;

    public ShopArtBoardLookupResponseDto(Long artRefId, Long shopId, String name, String content, String artImgPath, LocalDateTime createdAt, List<Long> categoryIdList, List<String> categoryTypeList) {
        this.artRefId = artRefId;
        this.shopId = shopId;
        this.name = name;
        this.content = content;
        this.artImgPath = artImgPath;
        this.createdAt = createdAt;
        this.categoryIdList = categoryIdList;
        this.categoryTypeList = categoryTypeList;
    }


}
