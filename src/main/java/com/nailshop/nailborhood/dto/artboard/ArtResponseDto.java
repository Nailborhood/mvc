package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArtResponseDto {

    private Long id;
    private String name;
    private String content;
    private Long likeCount;
    private String shopName;
    private List<String> categoryTypeList;
    private String mainImgPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ArtResponseDto(Long id, String name, String content, Long likeCount, String shopName, List<String> categoryTypeList, String mainImgPath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.likeCount = likeCount;
        this.shopName = shopName;
        this.categoryTypeList = categoryTypeList;
        this.mainImgPath = mainImgPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
