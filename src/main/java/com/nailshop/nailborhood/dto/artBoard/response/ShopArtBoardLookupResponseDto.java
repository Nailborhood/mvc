package com.nailshop.nailborhood.dto.artBoard.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ShopArtBoardLookupResponseDto {

    private Long artRefId;
    private String name;
    private String content;
    private String artImgPath;
    private LocalDateTime createdAt;

    public ShopArtBoardLookupResponseDto(Long artRefId, String name, String content, String artImgPath, LocalDateTime createdAt) {
        this.artRefId = artRefId;
        this.name = name;
        this.content = content;
        this.artImgPath = artImgPath;
        this.createdAt = createdAt;
    }


}
