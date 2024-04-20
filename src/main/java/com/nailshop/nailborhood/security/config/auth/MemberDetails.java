package com.nailshop.nailborhood.security.config.auth;

import com.nailshop.nailborhood.domain.member.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Data
public class MemberDetails implements UserDetails, OAuth2User {

    private final Member member;
    private Map<String, Object> attributes;

    // UserDetails
    public MemberDetails(Member member) {
        this.member = member;
    }

    // OAuth2User
    public MemberDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }


    // UserDetails 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority(member.getRole().toString())
                );
    }

    // UserDetails 비밀번호 리턴
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // UserDetails 이메일 리턴
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // UserDetails 만료 여부 리턴
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // UserDetails 계정 탈퇴 여부 리턴
    @Override
    public boolean isAccountNonLocked() {
        return !member.isDeleted();
    }

    // UserDetails 비밀번호 만효 여부 리턴 true : 만료안됨
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // UserDetails 계정 활성화 여부 리턴
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Custom 닉네임 리턴
    public String getNickname() { return member.getNickname(); }

    // OAuth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2User
    @Override
    public String getName() {
        String sub = attributes.get("sub").toString();
        return sub;
    }
}
