package com.nailshop.nailborhood.repository.review;

import com.nailshop.nailborhood.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    // shopId에 해당하는 리뷰 리스트
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT join r.shop s " +
            "WHERE s.shopId = :shopId AND s.isDeleted = false ")
    List<Review> findAllByShopIdAndIsDeleted(Long shopId);
}
