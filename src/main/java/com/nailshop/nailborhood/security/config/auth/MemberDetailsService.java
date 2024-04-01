package com.nailshop.nailborhood.security.config.auth;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (memberService.findByEmail(email)){
            // 이메일이 있는 경우
            Member member = memberRepository.findByEmail(email).get();
            if(member.isDeleted())
                return null;
            else {
                return new MemberDetails(member);
            }
        }
        return null;

    }

}
