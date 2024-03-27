package com.nailshop.nailborhood.repository.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom,Long> {
    // amdinId 가 포함된 모든 채팅룸 조회
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "WHERE cr.admin.adminId =:adminId")
    List<ChattingRoom> findAllByAdminId(@Param("adminId") Long adminId);
}
