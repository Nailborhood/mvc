package com.nailshop.nailborhood.dto.review.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewRegistrationRequestDto {
    private String contents;

    private Integer rate;

    public ReviewRegistrationRequestDto(String contents, Integer rate) {
        this.contents = contents;
        this.rate = rate;
    }
}
