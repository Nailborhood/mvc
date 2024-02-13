package com.nailshop.nailborhood.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewReportStatus {

    REVIEW_REPORT_PENDING("신고 대기중"),
    REVIEW_REPORT_IN_PROCESS("신고 처리중"),
    REVIEW_REPORT_REJECTED("신고 반려됨"),
    REVIEW_REPORT_ACCEPTED("신고 접수됨");

    private final String description;
}
