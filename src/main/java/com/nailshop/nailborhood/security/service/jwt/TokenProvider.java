package com.nailshop.nailborhood.security.service.jwt;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.security.config.jwt.JwtProperties;
import com.nailshop.nailborhood.security.dto.GeneratedToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    // 키 암호화 및 생성
    private String secretKey;
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes());
    }
    private SecretKey makeSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public GeneratedToken generateToken(Member member) {
        Date now = new Date();
        String refreshToken = makeRefreshToken(member, now);
        String accessToken = makeAccessToken(member, now);
        LocalDateTime accessTokenExpireTime = LocalDateTime.now().plus(30, ChronoUnit.MINUTES);
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plus(2, ChronoUnit.WEEKS);

        System.out.println(refreshToken);
        System.out.println(refreshTokenExpireTime);
        System.out.println(accessToken);
        System.out.println(accessTokenExpireTime);

        return new GeneratedToken(accessToken, refreshToken, accessTokenExpireTime, refreshTokenExpireTime);
    }



    private String makeRefreshToken(Member member, Date now) {
        //claim 생성
        Claims claims = Jwts.claims().setSubject(String.valueOf(member.getMemberId()));
        claims.put("role", member.getRole());

        // 토큰 유효기간 설정
        long refreshTokenPeriod = 1000L * 60L * 60L * 24L * 14L;
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() +Duration.ofDays(14).toMillis())) // 만료일시
                .addClaims(claims)
                .signWith(makeSecretKey())
                .compact();
    }

    private String makeAccessToken(Member member, Date now) {
        //claim 생성
        Claims claims = Jwts.claims().setSubject(String.valueOf(member.getMemberId()));
        claims.put("role", member.getRole());

        // 토큰 유효기간 설정
        long tokenPeriod = 1000L * 60L * 30L;
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis()))
                .addClaims(claims)
                .signWith(makeSecretKey())
                .compact();
    }

    public boolean verifyToken (String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(makeSecretKey())
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String role = String.valueOf(claims.get("role"));
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));

        return new UsernamePasswordAuthenticationToken(new User(claims.getSubject(), "", authorities), token, authorities);
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getRole(String token) {
        Claims claims = getClaims(token);
        return String.valueOf(claims.get("role"));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(makeSecretKey()).build().parseClaimsJws(token).getBody();
    }


}
