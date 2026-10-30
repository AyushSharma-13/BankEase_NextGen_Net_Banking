package com.bankease.repository;

import com.bankease.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerCustomerId(Long customerId);
    Optional<Account> findByAccountNo(String accountNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNo = :accountNo")
    Optional<Account> findByAccountNoWithLock(@Param("accountNo") String accountNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
    Optional<Account> findByAccountIdWithLock(@Param("accountId") Long accountId);
}
