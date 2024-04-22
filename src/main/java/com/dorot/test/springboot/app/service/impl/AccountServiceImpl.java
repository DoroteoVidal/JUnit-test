package com.dorot.test.springboot.app.service.impl;

import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.model.Bank;
import com.dorot.test.springboot.app.repository.AccountRepository;
import com.dorot.test.springboot.app.repository.BankRepository;
import com.dorot.test.springboot.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

//@Service, @Controller, @Repository, son anotaciones que permiten
// definir una clase/objeto que queremos registrar en el contenedor
// de Spring y luego podamos utilizar mediante la inyeccion de dependencias
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public int reviewTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();

        return bank.getTotalTransfers();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal reviewBalance(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();

        return account.getBalance();
    }

    @Override
    @Transactional
    public void transfer(Long sAccountId, Long dAccountId, BigDecimal amount, Long bankId) {
        Account sourceAccount = accountRepository.findById(sAccountId).orElseThrow();
        sourceAccount.debit(amount);
        accountRepository.save(sourceAccount);

        Account destinationAccount = accountRepository.findById(dAccountId).orElseThrow();
        destinationAccount.credit(amount);
        accountRepository.save(destinationAccount);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.save(bank);
    }
}
