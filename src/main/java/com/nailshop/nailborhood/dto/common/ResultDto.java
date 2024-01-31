package com.nailshop.nailborhood.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "in")
public class ResultDto<Data> {

    private final String status;
    private final String message;

    @Setter
    private Data data;
}
