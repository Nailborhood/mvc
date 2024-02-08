package com.nailshop.nailborhood.domain.category;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "category_art")
public class CategoryArt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_art_id")
    private Long categoryArtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref_id")
    private ArtRef artRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public CategoryArt(ArtRef artRef, Category category) {
        this.artRef = artRef;
        this.category = category;
    }
}
