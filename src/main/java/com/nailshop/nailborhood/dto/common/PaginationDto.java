package com.nailshop.nailborhood.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaginationDto {

    private int totalPages;
    private Long totalElements;
    private int pageNo;
    private boolean isLastPage;

    @Builder
    public PaginationDto(int totalPages, Long totalElements, int pageNo, boolean isLastPage) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNo = pageNo + 1;
        this.isLastPage = isLastPage;
    }
}
