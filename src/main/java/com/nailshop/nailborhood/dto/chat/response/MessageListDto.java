package com.nailshop.nailborhood.dto.chat.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class MessageListDto {
    List<MessageResponseDto> messageResponseDtoList;
}
