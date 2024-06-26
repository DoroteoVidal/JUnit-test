package com.dorot.test.springboot.app;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.dorot.test.springboot.app.Data.*;

import com.dorot.test.springboot.app.exception.InsufficientMoneyException;
import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.model.Bank;
import com.dorot.test.springboot.app.repository.AccountRepository;
import com.dorot.test.springboot.app.repository.BankRepository;
import com.dorot.test.springboot.app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class AccountServiceTest {

	@MockBean
	AccountRepository accountRepository;

	@MockBean
	BankRepository bankRepository;

	@Autowired
	AccountService accountService;

	@BeforeEach
	void setUp() {}

	@Test
	void canTransferBalanceFromOneAccountToAnother() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());
		when(accountRepository.findById(2L)).thenReturn(createAccount002());
		when(bankRepository.findById(1L)).thenReturn(createBank());

		BigDecimal sourceAccountBalance = accountService.reviewBalance(1L);
		BigDecimal destinationAccountBalance = accountService.reviewBalance(2L);

		assertEquals("1000", sourceAccountBalance.toPlainString());
		assertEquals("2000", destinationAccountBalance.toPlainString());

		accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

		sourceAccountBalance = accountService.reviewBalance(1L);
		destinationAccountBalance = accountService.reviewBalance(2L);

		assertEquals("900", sourceAccountBalance.toPlainString());
		assertEquals("2100", destinationAccountBalance.toPlainString());

		int total = accountService.reviewTotalTransfers(1L);

		assertEquals(1, total);

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(3)).findById(2L);
		verify(accountRepository, times(2)).save(any(Account.class));
		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository).save(any(Bank.class));
		verify(accountRepository, times(6)).findById(anyLong());
		verify(accountRepository, never()).findAll();
	}

	@Test
	void willThrowWhenTheBalanceIsInsufficient() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());
		when(accountRepository.findById(2L)).thenReturn(createAccount002());
		when(bankRepository.findById(1L)).thenReturn(createBank());

		BigDecimal sourceAccountBalance = accountService.reviewBalance(1L);
		BigDecimal destinationAccountBalance = accountService.reviewBalance(2L);

		assertEquals("1000", sourceAccountBalance.toPlainString());
		assertEquals("2000", destinationAccountBalance.toPlainString());
		assertThrows(InsufficientMoneyException.class, () -> {
			accountService.transfer(1L, 2L, new BigDecimal("1200"), 1L);
		});


		sourceAccountBalance = accountService.reviewBalance(1L);
		destinationAccountBalance = accountService.reviewBalance(2L);

		assertEquals("1000", sourceAccountBalance.toPlainString());
		assertEquals("2000", destinationAccountBalance.toPlainString());

		int total = accountService.reviewTotalTransfers(1L);

		assertEquals(0, total);

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(2)).findById(2L);
		verify(accountRepository, never()).save(any(Account.class));
		verify(bankRepository, times(1)).findById(1L);
		verify(bankRepository, never()).save(any(Bank.class));
		verify(accountRepository, times(5)).findById(anyLong());
		verify(accountRepository, never()).findAll();
	}

	@Test
	void equalsAccounts() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());

		Account account1 = accountService.findById(1L);
		Account account2 = accountService.findById(1L);

		//assertTrue(account1 == account2);
		assertSame(account1, account2);
		assertEquals("Andres", account1.getPerson());
		assertEquals("Andres", account2.getPerson());
		verify(accountRepository, times(2)).findById(1L);
	}

	@Test
	void findAllAccountsTest() {
		// Given
		List<Account> data = Arrays.asList(
				createAccount001().orElseThrow(),
				createAccount002().orElseThrow());
		when(accountService.findAll()).thenReturn(data);

		// When
		List<Account> accounts = accountService.findAll();

		// Then
		assertFalse(accounts.isEmpty());
		assertEquals(2, accounts.size());
		assertTrue(accounts.contains(createAccount002().orElseThrow()));
		verify(accountRepository).findAll();
	}

	@Test
	void saveAccountTest() {
		// Given
		Account pepe = new Account(null, "Pepe", new BigDecimal("3000"));
		when(accountRepository.save(any())).then(invocation -> {
			Account a = invocation.getArgument(0);
			a.setId(3L);

			return a;
		});

		// When
		Account account = accountService.save(pepe);

		// Then
		assertEquals("Pepe", account.getPerson());
		assertEquals(3, account.getId());
		assertEquals("3000", account.getBalance().toPlainString());
		verify(accountRepository).save(any());
	}
}
