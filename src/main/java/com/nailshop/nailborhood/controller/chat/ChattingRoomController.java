package com.nailshop.nailborhood.controller.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomDetailDto;
import com.nailshop.nailborhood.dto.chat.response.MessageListDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.chat.ChattingRoomService;
import com.nailshop.nailborhood.service.chat.MessageService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChattingRoomController {
    private final ChattingRoomService chattingRoomService;
    private final ShopDetailService shopDetailService;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    // 사장님 페이지
    // 채팅방 개설
    @GetMapping("/roomForm")
    public String roomForm() {
        return "owner/owner_chat_room_form";
    }

    @PostMapping("/chatroom")
    public String createChatRoom(RedirectAttributes redirectAttributes) {
        //TODO: member 연결 필요 session 에서 ownerId 가져와서 shopId 가져오기!
        Long ownerId = 1L;
        ChattingRoom chattingRoom = chattingRoomService.createChatRoom(ownerId);
        redirectAttributes.addFlashAttribute("roomId", chattingRoom.getRoomId());
        return "redirect:/chatroom/" + chattingRoom.getRoomId(); // 채팅방 입장 페이지로 리다이렉트
    }

    @GetMapping("/chatroom/{roomId}")
    public String joinRoom(@PathVariable("roomId")Long roomId, Model model) throws JsonProcessingException {
        try {
            //TODO: ownerId 연결 필요
            Long ownerId = 1L;
            // 채팅룸 정보
            ChattingRoomDetailDto chattingRoomDetailDto = chattingRoomService.findChatRoomId(roomId);
            // 매장 정보
            Shop shop = shopDetailService.findMyShopId(ownerId);
            CommonResponseDto<Object> shopDetail = shopDetailService.getMyShopDetail(shop.getShopId());
            ResultDto<MyShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
            resultDto.setData((MyShopDetailListResponseDto) shopDetail.getData());

            // 채팅 메세지
            List<Map<String,Object>> messageListDto = messageService.getMessageList(roomId);
            String messageResponseDtoListJson = objectMapper.writeValueAsString(messageListDto);
            model.addAttribute("shopDto", resultDto);
            model.addAttribute("chatRoomDto", chattingRoomDetailDto);
            model.addAttribute("messageResponseDtoListJson",messageResponseDtoListJson);

            return "owner/owner_chat_room_detail";

        }catch (NotFoundException e){
            model.addAttribute("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);
            return "owner/owner_chat_room_form";
        }
    }

}
