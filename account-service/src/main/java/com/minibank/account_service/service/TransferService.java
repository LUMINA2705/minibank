package com.minibank.account_service.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.minibank.account_service.domain.Account;
import com.minibank.account_service.domain.Transfer;
import com.minibank.account_service.dto.TransferRequest;
import com.minibank.account_service.dto.TransferResponse;
import com.minibank.account_service.exception.AccountNotFoundException;
import com.minibank.account_service.exception.BusinessRuleException;
import com.minibank.account_service.repository.AccountRepository;
import com.minibank.account_service.repository.TransferRepository;

import jakarta.transaction.Transactional;

@Service
public class TransferService {

	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;
	private final TransactionIdGenerator transactionIdGenerator;

	public TransferService(AccountRepository accountRepository, TransferRepository transferRepository,
			TransactionIdGenerator transactionIdGenerator) {
		this.accountRepository = accountRepository;
		this.transferRepository = transferRepository;
		this.transactionIdGenerator = transactionIdGenerator;
	}

	@Transactional
	public TransferResponse transfer(String idempotencyKey, TransferRequest request) {

		// ---------- 5b: idempotency check ---------

		Optional<Transfer> existing = transferRepository.findByIdempotencyKey(idempotencyKey);
		if (existing.isPresent()) {
			Transfer previous = existing.get();

			if (!isSameRequest(previous, request)) {
				throw new BusinessRuleException("IDEMPOTENCY_KEY_REUSED",
						"Idempotency-Key was already used for a different transfer");
			}
			return TransferResponse.from(previous); // safe retry → same result, no money moved
		}
        // ---------- 5c: lock + debit + credit ----------
        UUID fromId = request.fromAccountId();
        UUID toId = request.toAccountId();

        if (fromId.equals(toId)) {
            throw new BusinessRuleException("SAME_ACCOUNT", "Cannot transfer to the same account");
        }

        // Lock both rows in a FIXED order (smaller id first) → no deadlocks
        UUID firstId  = fromId.compareTo(toId) < 0 ? fromId : toId;
        UUID secondId = fromId.compareTo(toId) < 0 ? toId : fromId;

        Account first  = lockAccount(firstId);
        Account second = lockAccount(secondId);

        // Work out which locked account is "from" and which is "to"
        Account from = first.getId().equals(fromId) ? first : second;
        Account to   = first.getId().equals(toId)   ? first : second;

        from.debit(request.amount());   // checks: positive, ACTIVE, enough balance
        to.credit(request.amount());    // checks: positive, ACTIVE
		// ---------- 5d: save + return (after) ----------
		return null; // temporary, so it compiles

	}

	private boolean isSameRequest(Transfer previous, TransferRequest request) {
		return previous.getFromAccountId().equals(request.fromAccountId())
				&& previous.getToAccountId().equals(request.toAccountId())
				&& previous.getAmount().compareTo(request.amount()) == 0;
	}
	
	 private Account lockAccount(UUID id) {
	        return accountRepository.findByIdForUpdate(id)
	                .orElseThrow(() -> new AccountNotFoundException(id));
	    }
}
