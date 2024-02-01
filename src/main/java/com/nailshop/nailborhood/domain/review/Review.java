package com.nailshop.nailborhood.domain.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private String contents;

    private String rate;

    private LocalDateTime created;

    private boolean isDeleted;

    @Builder
    public Review(Long reviewId, String contents, String rate, LocalDateTime created, boolean isDeleted) {
        this.reviewId = reviewId;
        this.contents = contents;
        this.rate = rate;
        this.created = created;
        this.isDeleted = isDeleted;
    }
}
