package com.minibank.account_service.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.NotNull;

public record AccountReply(
		UUID eventId,
		UUID transferId,
		CommandType commandType,
		boolean success,
		Instant occurredAt){

}
