package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Login;
import com.nailshop.nailborhood.repository.member.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final LoginRepository loginRepository;

    public Login findByRefreshToken(String refreshToken) {
        return loginRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
