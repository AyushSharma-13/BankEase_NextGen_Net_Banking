package com.bankease.repository;

import com.bankease.entity.BillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    List<BillPayment> findByCustomerCustomerIdOrderByCreatedAtDesc(Long customerId);
    Page<BillPayment> findByCustomerCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    Optional<BillPayment> findByReferenceNo(String referenceNo);
}
