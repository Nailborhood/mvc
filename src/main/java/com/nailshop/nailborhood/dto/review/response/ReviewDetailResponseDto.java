package com.nailshop.nailborhood.dto.review.response;

import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.ShopStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class ReviewDetailResponseDto {

    private Long reviewId;
    private Long shopId;
    private String shopName;
    private ShopStatus shopStatus;
    private String shopAddress;
    private String reviewReportStatus;
    private List<String> categoryTypeList;
    private Map<Integer, String> imgPathMap;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private String reviewAuthor;
    private String reviewAuthorProfileImg;
    private LocalDateTime reviewCreatedAt;
    private LocalDateTime reviewUpdatedAt;
    private Boolean reviewLikeStatus;
    private boolean isDeleted;
    private String writer;

}
