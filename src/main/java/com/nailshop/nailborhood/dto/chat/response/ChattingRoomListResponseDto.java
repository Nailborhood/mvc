package com.nailshop.nailborhood.dto.chat.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChattingRoomListResponseDto {
    private List<ChattingRoomDetailAndShopInfoDto> chattingRoomDetailAndShopInfoDtoList;
    private PaginationDto paginationDto;

}
