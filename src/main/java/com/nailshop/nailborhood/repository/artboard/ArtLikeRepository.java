package com.nailshop.nailborhood.repository.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtLikeRepository extends JpaRepository<ArtLike, Long> {
    Optional<ArtLike> findByMemberMemberIdAndArtRefArtRefId(Long memberId, Long artRefId);

    // 좋아요 토글
    @Query("UPDATE ArtLike al " +
            "SET al.status = :status " +
            "WHERE al.artLikeId = :id")
    @Modifying(clearAutomatically = true)
    void toggleStatus(@Param("id") Long artLikeId, @Param("status") boolean b);

    @Query("DELETE FROM ArtLike al WHERE al.artRef.artRefId = :artRefId")
    @Modifying(clearAutomatically = true)
    void deleteByArtRefId(@Param("artRefId") Long artRefId);
}
