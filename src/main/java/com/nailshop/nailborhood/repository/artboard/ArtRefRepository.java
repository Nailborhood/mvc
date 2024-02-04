package com.nailshop.nailborhood.repository.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtRefRepository extends JpaRepository<ArtRef, Long> {

    // ArtRefId로 존재 여부 확인
    @Query("SELECT a " +
            "FROM ArtRef a " +
            "WHERE a.artRefId = :artRefId AND a.isDeleted = false")
    Optional<ArtRef> findByArtRefIdAndIsDeleted(@Param("artRefId") Long artRefId);
}
