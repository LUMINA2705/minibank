package com.minibank.account_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minibank.account_service.domain.Account;
import com.minibank.account_service.domain.AccountStatus;
import com.minibank.account_service.domain.AccountType;
import com.minibank.account_service.dto.AccountResponse;
import com.minibank.account_service.dto.CreateAccountRequest;
import com.minibank.account_service.exception.AccountNotFoundException;
import com.minibank.account_service.exception.BusinessRuleException;
import com.minibank.account_service.repository.AccountRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@Mock
	AccountRepository accountRepository;

	@InjectMocks
	AccountService accountService;

	@Test
	void get_unknownId_throwsAccountNotFound() {
		UUID id = UUID.randomUUID();
		when(accountRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.findAccount(id))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessageContaining(id.toString());	
	}
	
    @Test
    void open_createsActiveInrAccountWithMbNumber() {
        // Arrange
        when(accountRepository.nextAccountNumber()).thenReturn(1000000001L);
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountRequest request =
                new CreateAccountRequest("CUST-1", AccountType.SAVINGS, new BigDecimal("500.00"));

        // Act
        AccountResponse response = accountService.open(request);

        // Assert
        assertThat(response.accountNumber()).isEqualTo("MB1000000001");
        assertThat(response.customerId()).isEqualTo("CUST-1");
        assertThat(response.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.currency()).isEqualTo("INR");
        assertThat(response.balance()).isEqualByComparingTo("500.00");
        assertThat(response.id()).isNotNull();

        verify(accountRepository).save(any(Account.class));
    }
    
    @Test
    void close_withBalance_throwsBalanceNotZero() {
        // Arrange: a real Account object with 100 rupees
        Account account = Account.open("MB1", "CUST-1", AccountType.SAVINGS, new BigDecimal("100.00"));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act + Assert
        assertThatThrownBy(() -> accountService.close(account.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", "BALANCE_NOT_ZERO");

        // and the account must NOT have been closed
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
    
    @Test
    void close_zeroBalance_closesAndIsIdempotent() {
        // Arrange
        Account account = Account.open("MB2", "CUST-1", AccountType.SAVINGS, BigDecimal.ZERO);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act: close twice
        accountService.close(account.getId());
        accountService.close(account.getId());   // must not throw

        // Assert
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        verify(accountRepository, never()).save(any());
    }
}
