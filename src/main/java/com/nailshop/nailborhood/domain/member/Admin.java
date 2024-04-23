package com.nailshop.nailborhood.domain.member;

import com.nailshop.nailborhood.domain.alarm.Alarm;
import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "admin")
    private List<ChattingRoom> chattingRoomList;
}
