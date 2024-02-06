package com.nailshop.nailborhood.repository.artBoard;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArtRefRepository extends JpaRepository <ArtRef,Long> {
    // 매장에 해당 하는 아트판 리스트 불러오기
    @Query("SELECT a " +
            "FROM ArtRef a " +
            "LEFT join a.shop s " +
            "WHERE s.shopId = :shopId AND s.isDeleted = false ")
    Page<ArtRef> findAllNotDeletedBYShopId(Pageable pageable, Long shopId);
}
