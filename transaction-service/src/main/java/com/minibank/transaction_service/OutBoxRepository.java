package com.minibank.transaction_service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minibank.transaction_service.messaging.OutboxEvent;

public interface OutBoxRepository extends JpaRepository<OutboxEvent, UUID>{

	
	List<OutboxEvent> findTop100BySentAtIsNullOrderByCreatedAtAsc();
}
