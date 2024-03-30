package com.nailshop.nailborhood.dto.chat.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageResponseDto {
    private Long messageId;
    private String writer;
    private String contents;
    private LocalDateTime createdAt;
}
