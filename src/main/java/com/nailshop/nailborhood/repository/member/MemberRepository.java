package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
