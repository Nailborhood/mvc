package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
