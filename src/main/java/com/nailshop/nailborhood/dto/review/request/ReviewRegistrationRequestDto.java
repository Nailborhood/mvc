package com.nailshop.nailborhood.dto.review.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewRegistrationRequestDto {
    private String contents;

    private Integer rate;

    private List<Long> categoryListId;
}
