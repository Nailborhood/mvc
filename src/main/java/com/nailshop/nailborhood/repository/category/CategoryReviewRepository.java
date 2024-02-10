package com.nailshop.nailborhood.repository.category;

import com.nailshop.nailborhood.domain.category.CategoryReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryReviewRepository extends JpaRepository<CategoryReview, Long> {

    void deleteByReviewReviewId(Long reviewId);
}
