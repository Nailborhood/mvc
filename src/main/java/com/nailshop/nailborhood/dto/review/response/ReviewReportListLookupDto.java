package com.nailshop.nailborhood.dto.review.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewReportListLookupDto {
    private PaginationDto paginationDto;
    private List<ReviewReportLookupDto> reviewReportLookupDtoList;
}
