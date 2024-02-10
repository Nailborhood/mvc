package com.nailshop.nailborhood.repository.category;

import com.nailshop.nailborhood.domain.category.CategoryReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryReviewRepository extends JpaRepository<CategoryReview, Long> {

    void deleteByReviewReviewId(Long reviewId);

    @Query("SELECT c.type " +
            "FROM CategoryReview cr " +
            "LEFT JOIN cr.category c " +
            "WHERE cr.review.reviewId = :reviewId ")
    List<String> findCategoryTypeByReviewId(@Param("reviewId") Long reviewId);
}
