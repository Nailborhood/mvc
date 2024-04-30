package com.nailshop.nailborhood.domain.artboard;

import com.nailshop.nailborhood.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "art_book_mark")
public class ArtBookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_book_mark_id")
    private  Long artBookMarkId;

    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref_id")
    private ArtRef artRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ArtBookMark(Boolean status, ArtRef artRef, Member member) {
        this.status = status;
        this.artRef = artRef;
        this.member = member;
    }
}
