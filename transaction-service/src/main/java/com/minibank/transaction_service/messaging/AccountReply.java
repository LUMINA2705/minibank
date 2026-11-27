package com.minibank.transaction_service.messaging;

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
