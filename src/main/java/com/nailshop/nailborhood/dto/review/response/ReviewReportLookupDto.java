package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewReportLookupDto {
    private Long reportId;

    private String reportContents;

    private LocalDateTime date;

    private String status;

    private Long reviewId;

    private String reviewContents;

    // TODO: 고객 닉네임
    // private String nickname;


    public ReviewReportLookupDto(Long reportId, String reportContents, LocalDateTime date, String status, Long reviewId, String reviewContents) {
        this.reportId = reportId;
        this.reportContents = reportContents;
        this.date = date;
        this.status = status;
        this.reviewId = reviewId;
        this.reviewContents = reviewContents;
    }
}
