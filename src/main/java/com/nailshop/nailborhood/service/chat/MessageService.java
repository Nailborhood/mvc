package com.nailshop.nailborhood.service.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.chat.Message;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.chat.request.MessageRequestDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.chat.ChattingRoomRepository;
import com.nailshop.nailborhood.repository.chat.MessageRepository;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    // 채팅 메세지 저장
    public void saveMessage(MessageRequestDto messageRequestDto) {

        ChattingRoom chattingRoom = chattingRoomRepository.findById(messageRequestDto.getRoomId())
                                                          .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        Message message = Message.builder()
                                 .contents(messageRequestDto.getContents())
                                 .writer(messageRequestDto.getWriter())
                                 .chattingRoom(chattingRoom)
                                 .build();
        messageRepository.save(message);
    }

    // 채팅룸에 해당되는 메세지 리스트 조회
    public List<Map<String, Object>> getMessageList(Long roomId) {
        List<Message> messageList = messageRepository.findAllByRoomId(roomId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Message message : messageList) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", message.getMessageId());
            messageMap.put("writer", message.getWriter());
            messageMap.put("contents", message.getContents());
            messageMap.put("messageDate",message.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));

            resultList.add(messageMap);
        }

        return resultList;
    }



    /*    public MessageListDto getMessageList(Long roomId) {
        List<Message> messageList = messageRepository.findAllByRoomId(roomId);
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
        for (Message message : messageList) {
            MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                                                                      .messageId(message.getMessageId())
                                                                      .writer(message.getWriter())
                                                                      .contents(message.getContents())
                                                                      .build();

            messageResponseDtoList.add(messageResponseDto);
        }

        return MessageListDto.builder()
                             .messageResponseDtoList(messageResponseDtoList)
                             .build();
    }*/
}
