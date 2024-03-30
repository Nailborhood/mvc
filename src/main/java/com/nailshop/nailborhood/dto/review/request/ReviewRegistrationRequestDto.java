package com.nailshop.nailborhood.dto.review.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ReviewRegistrationRequestDto {
    private String contents;

    private Integer rate;

    private List<Long> categoryIdList;
}
