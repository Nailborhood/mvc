package com.nailshop.nailborhood.repository.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
    // amdinId 가 포함된 모든 채팅룸 조회
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "WHERE cr.admin.adminId =:adminId ")
    List<ChattingRoom> findAllByAdminId(@Param("adminId") Long adminId);

    // 키워드 검색
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "WHERE (cr.roomName like %:keyword% ) AND cr.admin.adminId =:adminId ")
    Page<ChattingRoom> findAllChatListBySearch(@Param("keyword") String keyword, PageRequest pageable,@Param("adminId") Long adminId);

    // 채팅 목록 페이징
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "WHERE cr.admin.adminId =:adminId ")
    Page<ChattingRoom> findAll(PageRequest pageable, Long adminId);

    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "LEFT JOIN cr.owner.shop s " +
            "WHERE s.shopId =:shopId ")
    List<ChattingRoom> findAllByShopId(@Param("shopId") Long shopId);
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "LEFT JOIN cr.owner o " +
            "WHERE o.ownerId =:ownerId ")
    ChattingRoom findByOwnerId(@Param("ownerId") Long ownerId);

/*    @Query("UPDATE ChattingRoom  cr " +
            "SET cr.isDeleted = :status " +
            "WHERE cr.roomId = :roomId")
    @Modifying(clearAutomatically = true)
    void deleteByChattingRoomId(@Param("roomId") Long roomId, @Param("status") boolean status);*/


}
