package com.nailshop.nailborhood.controller.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomDetailDto;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.chat.ChattingRoomService;
import com.nailshop.nailborhood.service.chat.MessageService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/owner/roomForm")
    public String roomForm(@AuthenticationPrincipal MemberDetails memberDetails) {
        try {
            Member member = memberDetails.getMember();
            chattingRoomService.checkingChatRoom(member);
            return "owner/owner_chat_room_form";
        } catch (BadRequestException b) {
            Member member = memberDetails.getMember();
            ChattingRoomDetailDto chattingRoom = chattingRoomService.getMyChatRoom(member);
            return "redirect:/chatroom/" + chattingRoom.getRoomId();
        }


    }

    @PostMapping("/chatroom")
    public String createChatRoom(RedirectAttributes redirectAttributes, @AuthenticationPrincipal MemberDetails memberDetails) {

        Member member = memberDetails.getMember();
        ChattingRoom chattingRoom = chattingRoomService.createChatRoom(member);
        redirectAttributes.addFlashAttribute("roomId", chattingRoom.getRoomId());
        return "redirect:/chatroom/" + chattingRoom.getRoomId(); // 채팅방 입장 페이지로 리다이렉트
    }

    @GetMapping("/chatroom/{roomId}")
    public String joinRoom(@PathVariable("roomId") Long roomId,
                           Model model,
                           @AuthenticationPrincipal MemberDetails memberDetails) throws JsonProcessingException {
        try {


            String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
            model.addAttribute("memberNickname", nicknameSpace);

            Member member = memberDetails.getMember();
            // 채팅룸 정보
            ChattingRoomDetailDto chattingRoomDetailDto = chattingRoomService.findChatRoomId(roomId);
            // 매장 정보
            Shop shop = shopDetailService.findMyShopId(member.getOwner().getOwnerId());
            if (shop.getIsDeleted()) {
                model.addAttribute("shopErrorCode", ErrorCode.DELETED_SHOP);
            }
            CommonResponseDto<Object> shopDetail = shopDetailService.getMyShopDetail(member);
            ResultDto<MyShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
            resultDto.setData((MyShopDetailListResponseDto) shopDetail.getData());

            // 채팅 메세지
            List<Map<String, Object>> messageListDto = messageService.getMessageList(roomId);
            String messageResponseDtoListJson = objectMapper.writeValueAsString(messageListDto);
            model.addAttribute("shopDto", resultDto);
            model.addAttribute("chatRoomDto", chattingRoomDetailDto);
            model.addAttribute("messageResponseDtoListJson", messageResponseDtoListJson);


            return "owner/owner_chat_room_detail";

        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);
            return "owner/owner_chat_room_form";
        }
    }

    // 관리자 페이지

    // 채팅룸 리스트 조회
/*    @GetMapping("/admin/chat/list")
    public String getChatList(Model model){
        try{

            Long adminId =1L;
            // 채팅룸 리스트 & 채팅룸 해당 매장 정보 & 메세지 리스트
            List<ChattingRoomDetailAndShopInfoDto> chattingRoomDetailAndShopInfoDtoList = chattingRoomService.getChatRoomList(adminId);

            model.addAttribute("chatRoomDto",chattingRoomDetailAndShopInfoDtoList);

            return "admin/admin_chat_list";
        }catch (NotFoundException e){
            return "admin/admin_chat_list";
        }

    }*/

    // 채팅룸 검색
    @GetMapping("/admin/search/chat")
    public String searchChatList(Model model,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                 @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                 @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
                                 @AuthenticationPrincipal MemberDetails memberDetails) {
        try {

            Long adminId = memberDetails.getMember().getAdmin().getAdminId();
            // 채팅룸 리스트 & 채팅룸 해당 매장 정보 & 메세지 리스트
            CommonResponseDto<Object> allChatList = chattingRoomService.searchChatRoomList(adminId, keyword, page, size, sortBy);
            ResultDto<ChattingRoomListResponseDto> resultDto = ResultDto.in(allChatList.getStatus(), allChatList.getMessage());
            resultDto.setData((ChattingRoomListResponseDto) allChatList.getData());

            model.addAttribute("resultDto", resultDto);


            return "admin/admin_chat_search_list";
        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);
            return "admin/admin_chat_search_list";
        }

    }

    // 채팅룸 Id 상세 조회
    @GetMapping("/admin/chatroom/{roomId}")
    public String getChatRoom(@PathVariable("roomId") Long roomId,
                              Model model,
                              @AuthenticationPrincipal MemberDetails memberDetails) throws JsonProcessingException {
        try {

            String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
            model.addAttribute("memberNickname", nicknameSpace);

            // 채팅룸 정보
            ChattingRoomDetailDto chattingRoomDetailDto = chattingRoomService.findChatRoomId(roomId);
            // 매장 정보
            Shop shop = shopDetailService.findMyShopId(chattingRoomDetailDto.getOwnerId());
            if (shop.getIsDeleted()) {
                model.addAttribute("shopErrorCode", ErrorCode.DELETED_SHOP);
            }
            CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetailByAdmin(shop.getShopId());
            ResultDto<MyShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
            resultDto.setData((MyShopDetailListResponseDto) shopDetail.getData());

            // 채팅 메세지
            List<Map<String, Object>> messageListDto = messageService.getMessageList(roomId);
            String messageResponseDtoListJson = objectMapper.writeValueAsString(messageListDto);

            model.addAttribute("shopDto", resultDto);
            model.addAttribute("chatRoomDto", chattingRoomDetailDto);
            model.addAttribute("messageResponseDtoListJson", messageResponseDtoListJson);


            return "admin/admin_chat_room_detail";

        } catch (NotFoundException e) {
            model.addAttribute("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);
            return "admin/admin_chat_list";
        }
    }

}
