package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewReportLookupDto {
    private Long reportId;

    private String reportContents;

    private LocalDateTime date;

    private String status;

    private Long reviewId;

    private String reviewContents;

    private String nickname;


    @Builder
    public ReviewReportLookupDto(Long reportId, String reportContents, LocalDateTime date, String status, Long reviewId, String reviewContents,String nickname) {
        this.reportId = reportId;
        this.reportContents = reportContents;
        this.date = date;
        this.status = status;
        this.reviewId = reviewId;
        this.reviewContents = reviewContents;
        this.nickname = nickname;
    }
}
