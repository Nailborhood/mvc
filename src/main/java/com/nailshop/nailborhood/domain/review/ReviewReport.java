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
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private String contents;

    private LocalDateTime date;

    private String status;

    @Builder
    public ReviewReport( String contents, LocalDateTime date, String status) {
        this.contents = contents;
        this.date = date;
        this.status = status;
    }
}
