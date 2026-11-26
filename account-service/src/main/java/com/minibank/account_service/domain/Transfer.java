package com.minibank.account_service.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Table(name="transfers")
@Entity
public class Transfer {
	
	@Id
	private UUID id;
	
	@Column(unique = true, nullable = false, updatable = false)
	private String reference;
	
	@Column(name="idempotency_key",unique = true, nullable = false, updatable = false)
	private String idempotencyKey;
	
	@Column(name = "from_account_id", nullable = false, updatable = false)
	private UUID fromAccountId;
	
	@Column(name = "to_account_id",  nullable = false, updatable = false)
	private UUID toAccountId;
	
	@Column(nullable = false, precision = 19, scale = 2, updatable = false)
	private BigDecimal amount;
	
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(nullable = false, length = 3)
	private String currency;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TransferStatus status;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	

	protected Transfer() {
	}


	public UUID getId() {
		return id;
	}

	public String getReference() {
		return reference;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}


	public UUID getFromAccountId() {
		return fromAccountId;
	}


	public UUID getToAccountId() {
		return toAccountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public TransferStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now;
	}
	
	public static Transfer completed(String reference, String idempotencyKey,
            UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
		Transfer transfer=new Transfer();
		
		transfer.id=UUID.randomUUID();
		transfer.currency="INR";
		transfer.status=TransferStatus.COMPLETED;
		transfer.reference=reference;
		transfer.idempotencyKey=idempotencyKey;
		transfer.fromAccountId=fromAccountId;
		transfer.toAccountId=toAccountId;
		transfer.amount=amount;
			
		
		return transfer;
		
	}
	

}
