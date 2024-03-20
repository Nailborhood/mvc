package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewReportLookupDto {
    private Long reportId;

    private String mainImgPath;

    private String reportContents; // 신고 내용

    private LocalDateTime date; // 신고 일시

    private String status; // 신고 상태

    private Long reviewId;

    private String reviewer; // 리뷰 작성자

    private String reviewContents; // 리뷰 내용

    private String reporter; // 신고자

    private String shopName;



    @Builder
    public ReviewReportLookupDto(Long reportId, String mainImgPath,String reportContents, LocalDateTime date, String status, Long reviewId, String reviewer, String reviewContents, String reporter, String shopName) {
        this.reportId = reportId;
        this.mainImgPath = mainImgPath;
        this.reportContents = reportContents;
        this.date = date;
        this.status = status;
        this.reviewId = reviewId;
        this.reviewer = reviewer;
        this.reviewContents = reviewContents;
        this.reporter = reporter;
        this.shopName = shopName;
    }
}
