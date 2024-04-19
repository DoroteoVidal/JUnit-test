package com.dorot.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;

import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
public class JpaIntegrationTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void findByIdTest() {
        Optional<Account> account = accountRepository.findById(1L);
        assertTrue(account.isPresent());
        assertEquals("Andres", account.orElseThrow().getPerson());
    }

    @Test
    void findByPersonTest() {
        Optional<Account> account = accountRepository.findByPerson("Andres");
        assertTrue(account.isPresent());
        assertEquals("Andres", account.orElseThrow().getPerson());
        assertEquals("1000.00", account.orElseThrow().getBalance().toPlainString());
    }

    @Test
    void willThrowWhenThereIsNoPerson() {
        Optional<Account> account = accountRepository.findByPerson("Rod");
        //Abreviar la expresion lambda
        assertThrows(NoSuchElementException.class, account::orElseThrow);
        assertFalse(account.isPresent());
    }

    @Test
    void findAllTest() {
        List<Account> accounts = accountRepository.findAll();

        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    void saveTest() {
        // Given
        Account pepeAccount = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        Account account = accountRepository.save(pepeAccount);

        // Then
        assertEquals("Pepe", account.getPerson());
        assertEquals("3000", account.getBalance().toPlainString());
    }

    @Test
    void updateTest() {
        // Given
        Account pepeAccount = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        Account account = accountRepository.save(pepeAccount);

        // Then
        assertEquals("Pepe", account.getPerson());
        assertEquals("3000", account.getBalance().toPlainString());

        // When
        account.setBalance(new BigDecimal("3800"));
        Account updateAccount = accountRepository.save(account);

        // Then
        assertEquals("Pepe", updateAccount.getPerson());
        assertEquals("3800", updateAccount.getBalance().toPlainString());
    }

    @Test
    void deleteTest() {
        // Given
        Account account = accountRepository.findById(2L).orElseThrow();
        assertEquals("Jhon", account.getPerson());

        // When
        accountRepository.delete(account);

        // Then
        assertThrows(NoSuchElementException.class, () -> {
            accountRepository.findByPerson("Jhon").orElseThrow();
        });
        assertEquals(1, accountRepository.findAll().size());
    }
}
