package com.minibank.account_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest (

	@NotNull
	 UUID fromAccountId,
	
	@NotNull
	 UUID toAccountId,
	
	@NotNull @Positive @Digits(integer=17 ,fraction=2)
	 BigDecimal amount) {
	
}
