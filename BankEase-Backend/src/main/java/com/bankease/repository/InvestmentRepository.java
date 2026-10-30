package com.bankease.repository;

import com.bankease.entity.Investment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByCustomerCustomerIdOrderByCreatedAtDesc(Long customerId);
    Page<Investment> findByCustomerCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    List<Investment> findAllByOrderByCreatedAtDesc();
    Page<Investment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

