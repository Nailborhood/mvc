package com.nailshop.nailborhood.domain.member;

import com.nailshop.nailborhood.domain.review.Review;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviewList;

    @Builder
    public Customer(Long customerId, Member member, List<Review> reviewList) {
        this.customerId = customerId;
        this.member = member;
        this.reviewList = reviewList;
    }
}
