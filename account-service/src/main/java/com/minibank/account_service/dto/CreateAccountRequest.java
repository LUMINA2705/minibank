package com.minibank.account_service.dto;

import java.math.BigDecimal;

import com.minibank.account_service.domain.AccountType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(@NotBlank @Size(max = 64) String customerId, @NotNull AccountType type,
		@NotNull @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal initialDeposit) {

}
