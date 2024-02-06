package com.nailshop.nailborhood.repository.category;

import com.nailshop.nailborhood.domain.category.CategoryArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryArtRepository extends JpaRepository<CategoryArt, Long> {

    void deleteByArtRefArtRefId(Long artRefId);

    // artRefId로 Category type 가져오기
    @Query("SELECT c.type FROM CategoryArt ca JOIN ca.category c WHERE ca.artRef.artRefId = :artRefId")
    List<String> findCategoryTypesByArtRefId(@Param("artRefId") Long artRefId);
}
