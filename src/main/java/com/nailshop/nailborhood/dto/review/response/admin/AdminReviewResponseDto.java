package com.nailshop.nailborhood.dto.review.response.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminReviewResponseDto {

    private Long reviewId;
    private String mainImgPath;
    private List<String> categoryTypeList;
    private String shopName;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String reviewer;
    private boolean isDeleted;
    private Long shopId;


}
