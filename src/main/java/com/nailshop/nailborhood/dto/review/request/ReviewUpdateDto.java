package com.nailshop.nailborhood.dto.review.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewUpdateDto {

    private Long reviewId;
    private String contents;
    private Integer rate;
//    private List<Long> categoryListId;

}