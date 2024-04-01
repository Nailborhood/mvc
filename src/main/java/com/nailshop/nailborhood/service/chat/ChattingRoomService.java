package com.nailshop.nailborhood.service.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.chat.Message;
import com.nailshop.nailborhood.domain.member.Admin;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomDetailAndShopInfoDto;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomDetailDto;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomListResponseDto;
import com.nailshop.nailborhood.dto.chat.response.MessageResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MyShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.chat.ChattingRoomRepository;
import com.nailshop.nailborhood.repository.chat.MessageRepository;
import com.nailshop.nailborhood.repository.member.AdminRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattingRoomService {
    private final ShopRepository shopRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;
    private final ShopDetailService shopDetailService;
    private final MessageRepository messageRepository;
    private final CommonService commonService;

    // 채탕방 개설
    public ChattingRoom createChatRoom(Long ownerId) {
        //TODO: ownerId -> session 으로 변경 필요
        Shop shop = shopRepository.findAllShopListByOwnerId(ownerId);
        String shopName = shop.getName();

        //owner 정보 가져오기
        Owner owner = ownerRepository.findByOwnerId(ownerId)
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));


        // Admin 정보 가져오기 TODO: 이렇게 지정하는게 맞는지 확인 필요
        Long adminId = 1L;
        Admin admin = adminRepository.findById(adminId)
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        ChattingRoom chattingRoom = ChattingRoom.builder()
                                                .roomName(shopName)
                                                .admin(admin)
                                                .owner(owner)
                                                .build();

        return chattingRoomRepository.save(chattingRoom);
    }

    // 채팅룸 id 에 해당되는 채팅룸 정보 조회

    public ChattingRoomDetailDto findChatRoomId(Long roomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId)
                                                          .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        return ChattingRoomDetailDto.builder()
                                    .roomId(chattingRoom.getRoomId())
                                    .roomName(chattingRoom.getRoomName())
                                    .adminId(chattingRoom.getAdmin()
                                                         .getAdminId())
                                    .ownerId(chattingRoom.getOwner()
                                                         .getOwnerId())
                                    .build();
    }

    // 채팅룸 전체 조회(관리자)
    public List<ChattingRoomDetailAndShopInfoDto> getChatRoomList(Long adminId) {
        List<ChattingRoom> chattingRoomList = chattingRoomRepository.findAllByAdminId(adminId);

        List<ChattingRoomDetailAndShopInfoDto> chattingRoomDetailAndShopInfoDtoList = new ArrayList<>();
        for (ChattingRoom room : chattingRoomList) {

            // 채팅룸에 해당되는 매장 정보
            Shop shop = shopDetailService.findMyShopId(room.getOwner()
                                                           .getOwnerId());
            CommonResponseDto<Object> shopDetail = shopDetailService.getMyShopDetail(shop.getShopId());
            MyShopDetailListResponseDto shopDetailData = (MyShopDetailListResponseDto) shopDetail.getData();


            // 채팅룸에 해당되는 메세지 리스트
            List<Message> messageList = messageRepository.findAllByRoomId(room.getRoomId());
            List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
            for (Message message : messageList) {
                MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                                                                          .messageId(message.getMessageId())
                                                                          .contents(message.getContents())
                                                                          .writer(message.getWriter())
                                                                          .createdAt(message.getCreatedAt())
                                                                          .build();
                messageResponseDtoList.add(messageResponseDto);
            }

            ChattingRoomDetailAndShopInfoDto chattingRoomDetailAndShopInfoDto = ChattingRoomDetailAndShopInfoDto.builder()
                                                                                                                .roomId(room.getRoomId())
                                                                                                                .roomName(room.getRoomName())
                                                                                                                .ownerId(room.getOwner()
                                                                                                                             .getOwnerId())
                                                                                                                .adminId(room.getAdmin()
                                                                                                                             .getAdminId())
                                                                                                                .myShopDetailListResponseDto(shopDetailData)
                                                                                                                .messageResponseDtoList(messageResponseDtoList)
                                                                                                                .build();

            chattingRoomDetailAndShopInfoDtoList.add(chattingRoomDetailAndShopInfoDto);
        }

        return chattingRoomDetailAndShopInfoDtoList;
    }

    // 채팅 룸 검색 (관리자)
    public CommonResponseDto<Object> searchChatRoomList(Long adminId, String keyword, int page, int size, String sortBy) {
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());

        Page<ChattingRoom> chatSearchPage;
        if (keyword == null || keyword.trim()
                                      .isEmpty()) {
            chatSearchPage = chattingRoomRepository.findAll(pageable, adminId);
        } else {
            chatSearchPage = chattingRoomRepository.findAllChatListBySearch(keyword, pageable, adminId);
        }
        if (chatSearchPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        List<ChattingRoom> chattingRoomList = chatSearchPage.getContent();
        List<ChattingRoomDetailAndShopInfoDto> chattingRoomDetailAndShopInfoDtoList = new ArrayList<>();

        for (ChattingRoom room : chattingRoomList) {

            // 채팅룸에 해당되는 매장 정보
            Shop shop = shopDetailService.findMyShopId(room.getOwner()
                                                           .getOwnerId());
            CommonResponseDto<Object> shopDetail = shopDetailService.getMyShopDetail(shop.getShopId());
            MyShopDetailListResponseDto shopDetailData = (MyShopDetailListResponseDto) shopDetail.getData();


            // 채팅룸에 해당되는 메세지 리스트
            List<Message> messageList = messageRepository.findAllByRoomId(room.getRoomId());
            List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
            for (Message message : messageList) {
                MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                                                                          .messageId(message.getMessageId())
                                                                          .contents(message.getContents())
                                                                          .writer(message.getWriter())
                                                                          .createdAt(message.getCreatedAt())
                                                                          .build();
                messageResponseDtoList.add(messageResponseDto);
            }

            ChattingRoomDetailAndShopInfoDto chattingRoomDetailAndShopInfoDto = ChattingRoomDetailAndShopInfoDto.builder()
                                                                                                                .roomId(room.getRoomId())
                                                                                                                .roomName(room.getRoomName())
                                                                                                                .ownerId(room.getOwner()
                                                                                                                             .getOwnerId())
                                                                                                                .adminId(room.getAdmin()
                                                                                                                             .getAdminId())
                                                                                                                .myShopDetailListResponseDto(shopDetailData)
                                                                                                                .messageResponseDtoList(messageResponseDtoList)
                                                                                                                .build();

            chattingRoomDetailAndShopInfoDtoList.add(chattingRoomDetailAndShopInfoDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(chatSearchPage.getTotalPages())
                                                   .totalElements(chatSearchPage.getTotalElements())
                                                   .pageNo(chatSearchPage.getNumber())
                                                   .isLastPage(chatSearchPage.isLast())
                                                   .build();

        ChattingRoomListResponseDto chattingRoomListResponseDto = ChattingRoomListResponseDto.builder()
                                                                                             .chattingRoomDetailAndShopInfoDtoList(chattingRoomDetailAndShopInfoDtoList)
                                                                                             .paginationDto(paginationDto)
                                                                                             .build();

        return commonService.successResponse(SuccessCode.SEARCH_BY_CHATROOM_SUCCESS.getDescription(), HttpStatus.OK, chattingRoomListResponseDto);
    }
}
