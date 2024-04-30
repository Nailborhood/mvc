package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ArtResponseDto {

    private Long id;
    private String name;
    private String content;
    private Long likeCount;
    private String shopName;
    private List<Long> categoryIdList;
    private List<String> categoryTypeList;
    private String mainImgPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long bookMarkCount;
    private String shopAddress;


}
