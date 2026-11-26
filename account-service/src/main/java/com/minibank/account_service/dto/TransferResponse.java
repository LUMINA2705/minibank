package com.minibank.account_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.minibank.account_service.domain.Transfer;
import com.minibank.account_service.domain.TransferStatus;

import jakarta.validation.constraints.NotNull;

public record TransferResponse(
		 UUID id,
		 String reference,
		UUID fromAccountId,
		UUID toAccountId,
		BigDecimal amount,
		String currecny,
		TransferStatus status,
		Instant createdAt)
{
	
	public static TransferResponse from(Transfer t) {
		return new TransferResponse(
				t.getId(),
				t.getReference(),
				t.getFromAccountId(),
				t.getToAccountId(), 
				t.getAmount(),
				t.getCurrency(),
				t.getStatus(),
				t.getCreatedAt());
	}
} 
