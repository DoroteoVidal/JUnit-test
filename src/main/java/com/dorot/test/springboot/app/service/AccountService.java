package com.dorot.test.springboot.app.service;

import com.dorot.test.springboot.app.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<Account> findAll();

    Account findById(Long id);

    Account save(Account account);

    void deleteById(Long id);

    int reviewTotalTransfers(Long bankId);

    BigDecimal reviewBalance(Long id);

    void transfer(Long sAccountId, Long dAccountId, BigDecimal amount, Long bankId);
}
