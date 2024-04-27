package com.nailshop.nailborhood.repository.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtBookMark;
import com.nailshop.nailborhood.domain.artboard.ArtLike;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtBookMarkRepository extends JpaRepository<ArtBookMark, Long> {
    Optional<ArtBookMark> findByMemberMemberIdAndArtRefArtRefId(Long memberId, Long artRefId);

    // 북마크 토글
    @Query("UPDATE ArtBookMark ab " +
            "SET ab.status = :status " +
            "WHERE ab.artBookMarkId = :id")
    @Modifying(clearAutomatically = true)
    void toggleStatus(@Param("id") Long artLikeId, @Param("status") boolean b);

    // 내가 북마크한 아트 조회
    @Query("SELECT abm.artRef " +
            "FROM ArtBookMark abm " +
            "LEFT JOIN abm.member m " +
            "WHERE abm.member.memberId = :memberId AND abm.status = true ")
    Page<ArtRef> findArtBookMarkListByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
