package com.nailshop.nailborhood.domain.review;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.common.BaseTime;
import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.shop.Shop;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    private String contents;

    private String rate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "review")
    private List<ReviewImg> reviewImgList;

    @OneToMany(mappedBy = "review")
    private List<ReviewReport> reviewReportList;

    @OneToMany(mappedBy = "review")
    private List<ReviewLike> reviewLikeList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Builder
    public Review( String contents, String rate, boolean isDeleted) {
        this.contents = contents;
        this.rate = rate;
        this.isDeleted = isDeleted;
    }
}
