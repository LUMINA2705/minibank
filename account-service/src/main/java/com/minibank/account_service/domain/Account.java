package com.minibank.account_service.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.minibank.account_service.exception.BusinessRuleException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	private UUID id;

	@Column(name = "account_number", nullable = false, unique = true, updatable = false)
	private String accountNumber;

	@Column(name = "customer_id", updatable = false, nullable = false)
	private String customerId;

	@Enumerated(EnumType.STRING)
	private AccountType type;

	@Enumerated(EnumType.STRING)
	private AccountStatus status;

	@Column(precision = 19, scale = 2)
	private BigDecimal balance;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(nullable = false, length = 3)
	private String currency;

	@Version
	private Long version;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	protected Account() {
	}

	public static Account open(String accountNumber, String customerId, AccountType type, BigDecimal initialDeposit) {
		Account account = new Account();
		account.id = UUID.randomUUID();
		account.accountNumber = accountNumber;
		account.customerId = customerId;
		account.type = type;
		account.status = AccountStatus.ACTIVE;
		account.balance = initialDeposit;
		account.currency = "INR";
		return account;
	}

	public void changeStatus(AccountStatus newStatus) {
		this.status = newStatus;
	}

	private void requiredActiveStatusAndPostiveAmount(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessRuleException("INVALID_AMOUNT", "Amount must be greater than 0");
		}
		if (status != AccountStatus.ACTIVE) {
			throw new BusinessRuleException("ACCOUNT_NOT_ACTIVE", "Account " + accountNumber + " is " + status);
		}
	}

	public void debit(BigDecimal amount) {

		requiredActiveStatusAndPostiveAmount(amount);

		if (balance.compareTo(amount) < 0) {
			throw new BusinessRuleException("INSUFFICIENT_FUNDS",
					"Insufficient funds: balance " + balance + ", requested " + amount);
		}
		balance = balance.subtract(amount);
	}

	public void credit(BigDecimal amount) {
		requiredActiveStatusAndPostiveAmount(amount);

		balance = balance.add(amount);
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getCustomerId() {
		return customerId;
	}

	public AccountType getType() {
		return type;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public String getCurrency() {
		return currency;
	}

	public Long getVersion() {
		return version;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

}
