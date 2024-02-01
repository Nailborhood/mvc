package com.nailshop.nailborhood.domain.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    private boolean status;

    @Builder
    public ReviewLike(Long likeId, boolean status) {
        this.likeId = likeId;
        this.status = status;
    }
}
