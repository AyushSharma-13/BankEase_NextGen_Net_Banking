package com.bankease.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long billId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 50)
    private String category; // ELECTRICITY, WATER, MOBILE_RECHARGE, INTERNET, GAS

    @Column(name = "biller_name", nullable = false, length = 100)
    private String billerName;

    @Column(name = "consumer_number", nullable = false, length = 50)
    private String consumerNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PAID"; // PENDING, PROCESSING, PAID, FAILED

    @Column(name = "reference_no", nullable = false, unique = true, length = 64)
    private String referenceNo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
