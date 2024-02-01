package com.nailshop.nailborhood.domain.address;

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
public class Districts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long distirictsId;

    private String name;

    @Builder
    public Districts(String name) {
        this.name = name;
    }
}
