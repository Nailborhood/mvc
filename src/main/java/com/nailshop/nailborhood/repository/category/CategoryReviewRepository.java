package com.nailshop.nailborhood.repository.category;

import com.nailshop.nailborhood.domain.category.CategoryReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryReviewRepository extends JpaRepository<CategoryReview, Long> {

    // reviewID에 해당하는 카테고리 리뷰 컬럼 삭제
    @Query("DELETE CategoryReview cr " +
            "WHERE cr.review " +
            "IN " +
            "(SELECT r " +
            "  FROM Review r " +
            "  WHERE r.reviewId = :reviewId)")
    @Modifying(clearAutomatically = true)
    void deleteByReviewReviewId(Long reviewId);

    @Query("SELECT c.type " +
            "FROM CategoryReview cr " +
            "LEFT JOIN cr.category c " +
            "WHERE cr.review.reviewId = :reviewId ")
    List<String> findCategoryTypeByReviewId(@Param("reviewId") Long reviewId);
}
