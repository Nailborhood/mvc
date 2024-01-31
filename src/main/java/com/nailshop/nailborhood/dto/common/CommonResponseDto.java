package com.nailshop.nailborhood.dto.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class CommonResponseDto<Data> {

    private String status;
    private String message;
    private HttpStatus httpStatus;
    private Data data;
}
