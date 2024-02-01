package com.nailshop.nailborhood.domain.review;

import com.nailshop.nailborhood.domain.common.BaseTime;
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
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private String contents;

    private String rate;

    private boolean isDeleted;

    @Builder
    public Review( String contents, String rate, boolean isDeleted) {
        this.contents = contents;
        this.rate = rate;
        this.isDeleted = isDeleted;
    }
}
