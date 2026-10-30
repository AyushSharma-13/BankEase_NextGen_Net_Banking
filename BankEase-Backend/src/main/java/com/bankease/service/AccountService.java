package com.bankease.service;

import com.bankease.dto.response.AccountDto;
import com.bankease.dto.response.DashboardOverviewDto;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAccountsByCustomerId(Long customerId);
    AccountDto getAccountById(Long accountId, Long customerId);
    AccountDto getAccountByAccountNo(String accountNo);
    DashboardOverviewDto getDashboardOverview(Long customerId);
}

