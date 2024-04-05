package com.nailshop.nailborhood.controller.chat;

import com.nailshop.nailborhood.domain.chat.Message;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.chat.request.MessageRequestDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    /* spring framework 에서 제공하는 인터페이스
    메시지를 WebSocket 세션에 보내는 작업을 추상화
    private final SimpMessageSendingOperations messageSendingOperations; // 메세지 전송 기능 구현*/
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    //pub/chatroom/{roomId}
    @MessageMapping("/chatroom/{roomId}") // 클라이언트에서 /send/chatroom/{roomId}로 메시지를 보낼 때 해당 메소드가 호출됩니다.
    public void sendMessage(@DestinationVariable Long roomId, MessageRequestDto messageRequestDto
    ) {



        messageService.saveMessage(messageRequestDto);

        // 메시지를 해당 채팅방 ID를 구독하고 있는 클라이언트들에게 전달
        messagingTemplate.convertAndSend("/sub/chatroom/" + roomId, messageRequestDto);
    }


}
