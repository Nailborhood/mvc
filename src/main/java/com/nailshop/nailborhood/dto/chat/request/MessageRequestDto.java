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
    // TODO: session 연결 이후로는 삭제
    private String writer;

}
