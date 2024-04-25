package com.nailshop.nailborhood.repository.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtBookMark;
import com.nailshop.nailborhood.domain.artboard.ArtLike;
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
}
