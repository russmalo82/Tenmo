package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalance(long user_id);

    void updateAccount(Account account, long user_id);

    long getAccountID(long user_id);

    Account getAccountByUserId(long user_id);
}
