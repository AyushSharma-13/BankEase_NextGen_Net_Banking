package com.bankease.repository;

import com.bankease.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByUserUserIdAndPurposeAndVerifiedFalseOrderByCreatedAtDesc(Long userId, String purpose);
    Optional<OtpVerification> findTopByUserUserIdAndOtpCodeAndPurposeAndVerifiedFalseAndExpiresAtAfter(
            Long userId, String otpCode, String purpose, LocalDateTime now);
}
