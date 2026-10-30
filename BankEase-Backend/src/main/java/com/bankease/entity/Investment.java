package com.bankease.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_id")
    private Long investmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "investment_type", nullable = false, length = 50)
    private String investmentType; // FIXED_DEPOSIT, RECURRING_DEPOSIT, MUTUAL_FUNDS, NATIONAL_PENSION_SCHEME

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "expected_return_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal expectedReturnRate;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE"; // REQUESTED, ACTIVE, COMPLETED, CANCELLED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
