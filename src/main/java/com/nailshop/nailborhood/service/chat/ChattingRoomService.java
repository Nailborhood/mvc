package com.nailshop.nailborhood.service.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.member.Admin;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.chat.response.ChattingRoomDetailDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.chat.ChattingRoomRepository;
import com.nailshop.nailborhood.repository.member.AdminRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChattingRoomService {
    private final ShopRepository shopRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    public ChattingRoom createChatRoom(Long ownerId) {
        //TODO: ownerId -> session 으로 변경 필요
        Shop shop = shopRepository.findAllShopListByOwnerId(ownerId);
        String shopName = shop.getName();

        //owner 정보 가져오기
        Owner owner = ownerRepository.findById(ownerId)
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        ;

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

    public ChattingRoomDetailDto findChatRoomId(Long roomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId)
                                                          .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        return ChattingRoomDetailDto.builder()
                                    .roomId(chattingRoom.getRoomId())
                                    .roomName(chattingRoom.getRoomName())
                                    .build();
    }
}
