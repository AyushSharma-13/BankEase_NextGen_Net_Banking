package com.bankease.repository;

import com.bankease.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findBySourceAccountCustomerCustomerIdOrderByCreatedAtDesc(Long customerId);
    Page<Transfer> findBySourceAccountCustomerCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    Optional<Transfer> findByReferenceNo(String referenceNo);
}

