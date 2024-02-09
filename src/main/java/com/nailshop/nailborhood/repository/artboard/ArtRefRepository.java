package com.nailshop.nailborhood.repository.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ArtRefRepository extends JpaRepository<ArtRef, Long> {

    // ArtRefId로 존재 여부 확인
    @Query("SELECT a " +
            "FROM ArtRef a " +
            "WHERE a.artRefId = :artRefId AND a.isDeleted = false")
    Optional<ArtRef> findByArtRefIdAndIsDeleted(@Param("artRefId") Long artRefId);
    // 매장에 해당 하는 아트판 리스트 불러오기

    // id로 삭제
    @Query("UPDATE ArtRef a " +
            "SET a.isDeleted = :status " +
            "WHERE a.artRefId = :artRefId ")
    @Modifying(clearAutomatically = true)
    void deleteByArtRefId(@Param("artRefId") Long artRefId ,@Param("status") Boolean status);

    // 좋아요 증가
    @Query("UPDATE ArtRef a " +
            "SET a.likeCount = a.likeCount +1" +
            "WHERE a.artRefId = :artRefId")
    @Modifying(clearAutomatically = true)
    void increaseLikeCount(@Param("artRefId") Long artRefId);

    // 좋아요 감소
    @Query("UPDATE ArtRef a " +
            "SET a.likeCount = CASE WHEN a.likeCount > 0 " +
            "THEN (a.likeCount -1) " +
            "ELSE 0 END " +
            "WHERE a.artRefId = :artRefId")
    @Modifying(clearAutomatically = true)
    void decreaseLikeCount(@Param("artRefId") Long artRefId);

    // 좋아요 0으로 변경
    @Query("UPDATE ArtRef a " +
            "SET a.likeCount = 0 " +
            "WHERE a.artRefId = :artRefId")
    @Modifying(clearAutomatically = true)
    void makeLikeCountZero(@Param("artRefId") Long artRefId);

    // 전체 조회
    Page<ArtRef> findByIsDeletedFalse(Pageable pageable);

    // 매장에 해당 하는 아트판 리스트 불러오기
    @Query("SELECT a " +
            "FROM ArtRef a " +
            "LEFT join a.shop s " +
            "WHERE s.shopId = :shopId AND s.isDeleted = false ")
    Page<ArtRef> findAllNotDeletedBYShopId(Pageable pageable, @Param("shopId") Long shopId);

    // 매장 Id에 해당하는 아트판
    @Query("SELECT a " +
            "FROM ArtRef a " +
            "LEFT JOIN a.shop s " +
            "WHERE s.shopId = :shopId AND s.isDeleted = false")
    List<ArtRef> findAllByShopIdAndIsDeleted(Long shopId);


}
