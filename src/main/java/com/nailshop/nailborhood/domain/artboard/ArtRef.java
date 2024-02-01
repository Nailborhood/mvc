package com.nailshop.nailborhood.domain.artboard;

import com.nailshop.nailborhood.domain.common.BaseTime;
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
public class ArtRef extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String name;
    private String content;
    private Boolean isDeleted;

    @Builder
    public ArtRef(String name, String content, Boolean isDeleted) {
        this.name = name;
        this.content = content;
        this.isDeleted = isDeleted;
    }
}
