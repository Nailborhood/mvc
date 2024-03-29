package com.nailshop.nailborhood.repository.chat;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
    // amdinId 가 포함된 모든 채팅룸 조회
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "LEFT JOIN cr.owner.shop s " +
            "WHERE cr.admin.adminId =:adminId AND s.isDeleted =false ")
    List<ChattingRoom> findAllByAdminId(@Param("adminId") Long adminId);

    // 키워드 검색
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "LEFT JOIN cr.owner.shop s " +
            "WHERE (s.name like %:keyword% ) AND cr.admin.adminId =:adminId  AND s.isDeleted =false")
    Page<ChattingRoom> findAllChatListBySearch(@Param("keyword") String keyword, PageRequest pageable,@Param("adminId") Long adminId);

    // 채팅 목록 페이징
    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "LEFT JOIN cr.owner.shop s " +
            "WHERE cr.admin.adminId =:adminId AND s.isDeleted =false")
    Page<ChattingRoom> findAll(PageRequest pageable, Long adminId);

    @Query("SELECT cr " +
            "FROM ChattingRoom cr " +
            "LEFT JOIN cr.owner.shop s " +
            "WHERE s.shopId =:shopId AND s.isDeleted =false")
    List<ChattingRoom> findAllByShopId(@Param("shopId") Long shopId);


}
