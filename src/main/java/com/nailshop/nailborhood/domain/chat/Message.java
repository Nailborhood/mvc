package com.nailshop.nailborhood.domain.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private String contents;

    private LocalDateTime createdDate;

    @Builder
    public Message( String contents, LocalDateTime createdDate) {
        this.contents = contents;
        this.createdDate = createdDate;
    }
}
