package com.dorot.test.springboot.app.repository;

import com.dorot.test.springboot.app.model.Account;

import java.util.List;

public interface AccountRepository {
    List<Account> findAll();

    Account findById(Long id);

    void update(Account account);
}
