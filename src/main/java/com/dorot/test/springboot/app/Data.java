package com.dorot.test.springboot.app;

import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.model.Bank;

import java.math.BigDecimal;

public class Data {
    //public static final Account ACCOUNT_001 = new Account(1L, "Andres", new BigDecimal(1000));
    //public static final Account ACCOUNT_002 = new Account(2L, "Jhon", new BigDecimal(2000));
    //public static final Bank BANK = new Bank(1L, "Banco financiero", 0);

    public static Account createAccount001() {
        return new Account(1L, "Andres", new BigDecimal(1000));
    }

    public static Account createAccount002() {
        return new Account(2L, "Jhon", new BigDecimal(2000));
    }

    public static Bank createBank() {
        return new Bank(1L, "Banco financiero", 0);
    }
}
