package com.nailshop.nailborhood.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequestMapping("/echo")
public class EcoHandler extends TextWebSocketHandler {


    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);


    // 로그인 중 사용자
    List<WebSocketSession> sessions = new ArrayList<>();

    // 로그인중인 개별유저
    Map<String, WebSocketSession> users = new ConcurrentHashMap<>();


    // 클라이언트가 서버로 연결시
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String senderId = sendPushUsername(session); // 접속한 유저의 http세션을 조회하여 id를 얻는 함수
        if (senderId != null) {    // 로그인 값이 있는 경우만
            log(senderId + " 연결 됨");
            users.put(senderId, session);   // 로그인중 개별유저 저장
        }
    }

    // 클라이언트가 Data 전송 시
    /*@Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String senderId = sendPushUsername(session);
        // 특정 유저에게 보내기
        String msg = message.getPayload();
        if (msg != null) {
            String[] strs = msg.split(",");
            log(strs.toString());
            if (strs != null && strs.length == 4) {
                String alarmType = strs[0];
                String receiver = strs[1]; // 이메일
                String url = strs[2]; // 링크 URL
                Long alarmId = Long.valueOf(strs[3]); // 알람 id

                WebSocketSession targetSession = users.get(receiver); // 메시지를 받을 세션 조회

                // 실시간 접속시
                if (targetSession != null) {
                    // ex: [알림 타입] 내용이 등록되었습니다.
                    String messageContent = alarmType + "가 등록되었습니다.";
                    TextMessage tmpMsg = new TextMessage("<a target='_blank' href='" + url + "' style='color: black; text-decoration: none;'>" + messageContent + "</a>");
                    targetSession.sendMessage(tmpMsg);

                }
            }
        }

    }*/

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String senderId = sendPushUsername(session);
        String msg = message.getPayload();
        if (msg != null) {
            String[] strs = msg.split(",");
            if (strs != null && strs.length == 4) {
                String alarmType = strs[0];
                String receiver = strs[1];  // 이메일
                String url = strs[2];  // 링크 URL
                Long alarmId = Long.valueOf(strs[3]);  // 알람 ID

                WebSocketSession targetSession = users.get(receiver);  // 메시지를 받을 세션 조회

                System.out.println("receiver : "+ receiver);
                System.out.println("url : "+ url);
                System.out.println("alarmId : "+ alarmId);
                if (targetSession != null) {
                    // JSON 객체로 메시지 구성
                    Map<String, Object> messageData = new HashMap<>();
                    messageData.put("alarmType", alarmType);
                    messageData.put("url", url);
                    messageData.put("alarmId", alarmId);

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonMessage = mapper.writeValueAsString(messageData);
                    TextMessage tmpMsg = new TextMessage(jsonMessage);
                    targetSession.sendMessage(tmpMsg);
                }
            }
        }
    }


    // 연결 해제될 때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String senderId = sendPushUsername(session);
        if (senderId != null) {    // 로그인 값이 있는 경우만
            log(senderId + " 연결 종료됨");
            users.remove(senderId);
            sessions.remove(session);
        }
    }

    // 에러 발생시
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log(session.getId() + " 익셉션 발생: " + exception.getMessage());

    }

    // 로그 메시지
    private void log(String logger) {
        System.out.println(new Date() + " : " + logger);
    }
    // 웹소켓에 id 가져오기
    // 접속한 유저의 http세션을 조회하여 id를 얻는 함수
//    private String getMemberId(WebSocketSession session) {
//        Map<String, Object> httpSession = session.getAttributes();
//        String m_id = (String) httpSession.get("m_id"); // 세션에 저장된 m_id 기준 조회
//        return m_id==null? null: m_id;
//    }

    //알람을 보내는 유저(댓글작성, 좋아요 누르는 유저)
    private String sendPushUsername(WebSocketSession session) {
        String loginUsername;

        if (session.getPrincipal() == null) {
            loginUsername = null;
        } else {
            Authentication authentication = (Authentication) session.getPrincipal();
            loginUsername = authentication.getName(); // nickname으로 반환
        }
        return loginUsername;
    }
}


