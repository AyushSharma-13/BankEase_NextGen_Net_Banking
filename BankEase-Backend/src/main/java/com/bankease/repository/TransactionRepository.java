package com.bankease.repository;

import com.bankease.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccountAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    Page<Transaction> findByAccountCustomerCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    List<Transaction> findTop10ByAccountCustomerCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Transaction> findByReferenceNo(String referenceNo);

    @Query("SELECT t FROM Transaction t WHERE t.account.customer.customerId = :customerId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.referenceNo) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> filterTransactions(
            @Param("customerId") Long customerId,
            @Param("type") String type,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("search") String search,
            Pageable pageable
    );
}
