package com.dorot.test.springboot.app.repository;

import com.dorot.test.springboot.app.model.Bank;

import java.util.List;

public interface BankRepository {
    List<Bank> findAll();

    Bank findById(Long id);

    void update(Bank bank);
}
