package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ArtDetailResponseDto {

    private Long shopId;
    private Long artRefId;
    private String name;
    private String content;
    private Long likeCount;
    private String shopName;
    private List<String> categoryTypeList;
    private Map<Integer, String> imgPathMap;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean artLikeStatus;
    private Long bookMarkCount;
    private Boolean artBookMarkStatus;
    private String shopAddress;

}
