package com.nailshop.nailborhood.repository.alarm;

import com.nailshop.nailborhood.domain.alarm.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    @Query("SELECT a " +
            "FROM Alarm a " +
            "LEFT JOIN a.receiver r " +
            "LEFT JOIN a.sender s "+
            "WHERE r.memberId =:memberId AND a.isChecked = false AND s.memberId <> r.memberId ")
    Page<Alarm> findAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT count(*) " +
            "FROM Alarm a " +
            "LEFT JOIN a.receiver r " +
            "LEFT JOIN a.sender s "+
            "WHERE a.receiver.memberId =:receiverId AND a.isChecked = false AND s.memberId <> r.memberId ")
    int countByReceiverId(@Param("receiverId") Long receiverId);
}
