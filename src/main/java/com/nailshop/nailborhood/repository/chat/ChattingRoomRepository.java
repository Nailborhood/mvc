package com.nailshop.nailborhood.repository.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom,Long> {
}
