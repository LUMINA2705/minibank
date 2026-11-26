package com.minibank.account_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minibank.account_service.domain.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, UUID>{
	
	Optional<Transfer> findByIdempotencyKey(String idempotencyKey);

}
