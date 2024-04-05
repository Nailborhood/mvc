package com.nailshop.nailborhood.dto.chat.response;

import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChattingRoomDetailAndShopInfoDto {

    private Long roomId;
    private String roomName;
    private Long ownerId;
    private Long adminId;
    private MyShopDetailListResponseDto myShopDetailListResponseDto;
    private List<MessageResponseDto> messageResponseDtoList;
}
