package com.nailshop.nailborhood.service.member.admin;

import com.nailshop.nailborhood.domain.member.Admin;
import com.nailshop.nailborhood.repository.member.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminInfoService {
    private final AdminRepository adminRepository;


    public Admin getAdminInfo(Long memberId) {
        return adminRepository.findByMemberId(memberId);
    }
}
