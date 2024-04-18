package com.dorot.test.springboot.app.service;

import com.dorot.test.springboot.app.model.Account;

import java.math.BigDecimal;

public interface AccountService {
    Account findById(Long id);

    int reviewTotalTransfers(Long bankId);

    BigDecimal reviewBalance(Long id);

    void transfer(Long sAccountId, Long dAccountId, BigDecimal amount, Long bankId);
}
