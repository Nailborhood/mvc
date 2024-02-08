package com.nailshop.nailborhood.domain.category;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.review.Review;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    private String type;

    @OneToMany(mappedBy = "category")
    private List<CategoryArt> categoryArtList;

    @OneToMany(mappedBy = "category")
    private List<Review> reviewList;

    @Builder
    public Category(String type) {
        this.type = type;
    }
}
