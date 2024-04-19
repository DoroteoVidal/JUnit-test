package com.dorot.test.springboot.app;

import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.model.Bank;

import java.math.BigDecimal;
import java.util.Optional;

public class Data {
    //public static final Account ACCOUNT_001 = new Account(1L, "Andres", new BigDecimal(1000));
    //public static final Account ACCOUNT_002 = new Account(2L, "Jhon", new BigDecimal(2000));
    //public static final Bank BANK = new Bank(1L, "Banco financiero", 0);

    public static Optional<Account> createAccount001() {
        return Optional.of(new Account(1L, "Andres", new BigDecimal(1000)));
    }

    public static Optional<Account> createAccount002() {
        return Optional.of(new Account(2L, "Jhon", new BigDecimal(2000)));
    }

    public static Optional<Bank> createBank() {
        return Optional.of(new Bank(1L, "Banco financiero", 0));
    }
}
