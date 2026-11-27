package com.minibank.transaction_service.domain;

	
	

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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
		
		@Column(name = "failure_reason")
		private String failureReason;
		
		@Version
		private Long version;
		
		
		@Column(name = "created_at", nullable = false, updatable = false)
		private Instant createdAt;
		
		@Column(name = "updated_at", updatable = false)
		private Instant updatedAt;
		
		
		

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
			this.updatedAt=now;
		}
		
		@PreUpdate
		void onUpdate() {
			this.updatedAt = Instant.now();
		}		
		
		public static Transfer pending(String reference, String idempotencyKey,
	            UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
			Transfer transfer=new Transfer();
			
			transfer.id=UUID.randomUUID();
			transfer.currency="INR";
			transfer.status=TransferStatus.PENDING;
			transfer.reference=reference;
			transfer.idempotencyKey=idempotencyKey;
			transfer.fromAccountId=fromAccountId;
			transfer.toAccountId=toAccountId;
			transfer.amount=amount;
				
			
			return transfer;
			
		}
		//helper method
		private void requireStatus(TransferStatus... allowed) {
			for(TransferStatus s:allowed) {
				if(this.status==s) {
					return;}
			}
				throw new IllegalStateException("Transfer "+ reference +" is "+status
						+",expected one of" +java.util.Arrays.toString(allowed));
			
		}
		
		//State-change method	
		//pending -> debited
			public void markDebited() {
				requireStatus(TransferStatus.PENDING);
				this.status=TransferStatus.DEBITED;
			}
			
		//debited -> completed
			public void markCompleted() {
				requireStatus(TransferStatus.DEBITED);
				this.status=TransferStatus.COMPLETED;
			}

			//debited -> refunding
			public void markRefunding(String reason) {
			
				requireStatus(TransferStatus.DEBITED);
				this.status=TransferStatus.REFUNDING;
				this.failureReason=reason;
			}
			
			//pending or refunding -> failed
			public void markFailed(String reason) {
			requireStatus(TransferStatus.PENDING ,TransferStatus.REFUNDING);

				
			if(this.failureReason ==null) {
				this.failureReason=reason;
			}
			this.status=TransferStatus.FAILED;
			
	}

}
