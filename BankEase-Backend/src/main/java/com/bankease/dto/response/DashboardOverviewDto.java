package com.bankease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDto {
    private String customerName;
    private String email;
    private List<AccountDto> accounts;
    private BigDecimal totalBalance;
    private List<TransactionDto> recentTransactions;
    private long unreadNotificationsCount;
    private long totalBeneficiariesCount;
    private long activeLoansCount;
    private long activeInvestmentsCount;
}

