package com.nailshop.nailborhood.security.config.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class JwtProperties {
    @Value("${jwt.token.secret}")
    private String secret;

}
