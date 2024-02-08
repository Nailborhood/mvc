package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ArtDetailResponseDto {

    private String name;
    private String content;
    private Long likeCount;
    private String shopName;
    private List<String> categoryTypeList;
    private Map<Integer, String> imgPathMap;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ArtDetailResponseDto(String name, String content, Long likeCount, String shopName, List<String> categoryTypeList, Map<Integer, String> imgPathMap, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.content = content;
        this.likeCount = likeCount;
        this.shopName = shopName;
        this.categoryTypeList = categoryTypeList;
        this.imgPathMap = imgPathMap;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
