package com.minibank.transaction_service.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.NotNull;

public record AccountCommand(
		@NotNull @UniqueElements UUID eventId,
		UUID transferId,
		CommandType type,
		UUID accountId,
		BigDecimal amount,
		Instant occuredAt){


}
