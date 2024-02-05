package com.nailshop.nailborhood.repository.review;

import com.nailshop.nailborhood.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Query("SELECT count(r) FROM Review r WHERE r.shop.shopId = :shopId AND r.isDeleted = false")
    long countByShopIdAndIsDeletedFalse(@Param("shopId") Long shopId);
}
