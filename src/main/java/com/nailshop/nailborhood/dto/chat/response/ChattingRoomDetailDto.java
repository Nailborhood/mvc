package com.nailshop.nailborhood.dto.chat.response;

import lombok.*;

@Getter
@Setter
@Builder
public class ChattingRoomDetailDto {

    private Long roomId;
    private String roomName;
    private Long ownerId;
    private Long adminId;
}
