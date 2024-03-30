package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Long> {
}
