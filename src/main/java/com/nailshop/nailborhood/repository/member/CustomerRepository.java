package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
