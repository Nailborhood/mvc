package com.nailshop.nailborhood.domain.chat;

import com.nailshop.nailborhood.domain.common.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "chatting_room")
public class ChattingRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;


}
