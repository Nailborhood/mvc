package com.nailshop.nailborhood.domain.chat;

import com.nailshop.nailborhood.domain.common.BaseTime;
import com.nailshop.nailborhood.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "chat_message")
public class Message extends BaseTime {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    private String contents;

    private String writer; // owner nickname, admin nickname (admin)


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private ChattingRoom chattingRoom;




    @Builder
    public Message(String contents,String writer,ChattingRoom chattingRoom) {
        this.contents = contents;
        this.writer = writer;
        this.chattingRoom = chattingRoom;
    }
}
