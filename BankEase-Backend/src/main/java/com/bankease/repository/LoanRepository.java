package com.bankease.repository;

import com.bankease.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerCustomerIdOrderByCreatedAtDesc(Long customerId);
    Page<Loan> findByCustomerCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    List<Loan> findAllByOrderByCreatedAtDesc();
    Page<Loan> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

