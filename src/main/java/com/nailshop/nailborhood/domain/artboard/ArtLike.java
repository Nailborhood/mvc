package com.nailshop.nailborhood.domain.artboard;

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
public class ArtLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long artLikeId;

    private Boolean status;

    @Builder
    public ArtLike(Boolean status) {
        this.status = status;
    }
}
