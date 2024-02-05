package com.nailshop.nailborhood.repository.review;


import com.nailshop.nailborhood.domain.review.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
}
