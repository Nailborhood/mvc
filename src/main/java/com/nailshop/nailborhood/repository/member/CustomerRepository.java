package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByMember_MemberId(Long memberId);

    @Query("SELECT c " +
            "FROM Customer c " +
            "LEFT JOIN c.member m " +
            "WHERE m.memberId =:memberId")
    Customer findByMemberId(@Param("memberId") Long memberId);
}
