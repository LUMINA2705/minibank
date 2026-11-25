package com.minibank.account_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.minibank.account_service.domain.Account;
import com.minibank.account_service.domain.AccountStatus;
import com.minibank.account_service.domain.AccountType;

public record AccountResponse(
        UUID id,
        String accountNumber,
        String customerId,
        AccountType type,
        AccountStatus status,
        BigDecimal balance,
        String currency,
        Instant createdAt
) {

    public static AccountResponse from(Account a) {
        return new AccountResponse(
                a.getId(),
                a.getAccountNumber(),
                a.getCustomerId(),
                a.getType(),
                a.getStatus(),
                a.getBalance(),
                a.getCurrency(),
                a.getCreatedAt()
        );
    }
}