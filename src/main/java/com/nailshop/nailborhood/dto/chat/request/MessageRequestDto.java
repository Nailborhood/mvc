package com.nailshop.nailborhood.dto.chat.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageRequestDto {

    private Long roomId;
    private String contents;

    private String writer;

}
