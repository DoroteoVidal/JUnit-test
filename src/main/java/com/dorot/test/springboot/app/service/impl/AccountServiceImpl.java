package com.dorot.test.springboot.app.service.impl;

import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.model.Bank;
import com.dorot.test.springboot.app.repository.AccountRepository;
import com.dorot.test.springboot.app.repository.BankRepository;
import com.dorot.test.springboot.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
    public Account findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public int reviewTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId);

        return bank.getTotalTransfers();
    }

    @Override
    public BigDecimal reviewBalance(Long id) {
        Account account = accountRepository.findById(id);

        return account.getBalance();
    }

    @Override
    public void transfer(Long sAccountId, Long dAccountId, BigDecimal amount, Long bankId) {
        Account sourceAccount = accountRepository.findById(sAccountId);
        sourceAccount.debit(amount);
        accountRepository.update(sourceAccount);

        Account destinationAccount = accountRepository.findById(dAccountId);
        destinationAccount.credit(amount);
        accountRepository.update(destinationAccount);

        Bank bank = bankRepository.findById(bankId);
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.update(bank);
    }
}
