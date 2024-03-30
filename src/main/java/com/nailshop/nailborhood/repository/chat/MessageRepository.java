package com.nailshop.nailborhood.repository.chat;

import com.nailshop.nailborhood.domain.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m " +
            "FROM Message m " +
            "LEFT JOIN m.chattingRoom cr " +
            "WHERE cr.roomId =:roomId ")
    List<Message> findAllByRoomId(@Param("roomId") Long roomId);
}
