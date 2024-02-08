package com.nailshop.nailborhood.domain.review;

import com.nailshop.nailborhood.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "review_report")
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    private String contents;

    private LocalDateTime date;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ReviewReport( String contents, LocalDateTime date, String status,Review review,Member member) {
        this.contents = contents;
        this.date = date;
        this.status = status;
        this.review = review;
        this.member = member;
    }
}
