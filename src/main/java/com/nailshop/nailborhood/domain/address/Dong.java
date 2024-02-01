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
public class Dong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long dongId;

    private String name;

    @Builder
    public Dong(String name) {
        this.name = name;
    }
}
