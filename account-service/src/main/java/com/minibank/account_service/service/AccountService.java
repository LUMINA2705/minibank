package com.minibank.account_service.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minibank.account_service.domain.Account;
import com.minibank.account_service.domain.AccountStatus;
import com.minibank.account_service.dto.AccountResponse;
import com.minibank.account_service.dto.CreateAccountRequest;
import com.minibank.account_service.exception.AccountNotFoundException;
import com.minibank.account_service.exception.BusinessRuleException;
import com.minibank.account_service.repository.AccountRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Transactional
	public AccountResponse open(CreateAccountRequest accountRequest) {
		String accountNumber = "MB" + accountRepository.nextAccountNumber();
		Account newAccount = Account.open(accountNumber, accountRequest.customerId(), accountRequest.type(),
				accountRequest.initialDeposit());
		Account saved = accountRepository.save(newAccount);
		return AccountResponse.from(saved);

	}

	@Transactional(readOnly = true)
	public AccountResponse get(UUID id) {
		return AccountResponse.from(findAccount(id));
	}

	@Transactional(readOnly = true)
	public Account findAccount(UUID id) {

		return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));

	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> list(String customerId, Pageable pageable) {

		Page<Account> pages;
		if (customerId == null || customerId.isBlank()) {
			pages = accountRepository.findAll(pageable);
		} else {
			pages = accountRepository.findByCustomerId(customerId, pageable);
		}

		return pages.map(AccountResponse::from);

	}

	@Transactional
	public AccountResponse changeStatus(UUID id, AccountStatus newStatus) {
		Account account = findAccount(id);

		if (account.getStatus() == AccountStatus.CLOSED) {
			throw new BusinessRuleException("ACCOUNT_CLOSED", "A closed account cannot be changed");
		}

		account.changeStatus(newStatus);
		return AccountResponse.from(account);
	}

	@Transactional
	public void close(UUID id) {
		Account account = findAccount(id);
		if (account.getStatus() == AccountStatus.CLOSED) {
			return;
		}

		if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {

			throw new BusinessRuleException("BALANCE_NOT_ZERO",
					"Withdraw the remaining balance before closing (current: " + account.getBalance() + " )");
		}
		account.changeStatus(AccountStatus.CLOSED);

	}

}
