package com.nailshop.nailborhood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NailborhoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(NailborhoodApplication.class, args);
    }

}
