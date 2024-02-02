package com.nailshop.nailborhood.domain.chat;

import com.nailshop.nailborhood.domain.common.BaseTime;
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

    @Builder
    public Message( String contents) {
        this.contents = contents;
    }
}
