package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private JdbcAccountDao accountDao;
    public AccountController(JdbcAccountDao accountDao) {this.accountDao = accountDao;}

    @RequestMapping(path = "account/balance/{user_id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable long user_id)
    {
        return accountDao.getBalance(user_id);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "account/{user_id}", method = RequestMethod.PUT)
    public void updateAccount(@RequestBody Account account, @PathVariable long user_id)
    {
        accountDao.updateAccount(account, user_id);
    }

    @RequestMapping(path = "account/{user_id}", method = RequestMethod.GET)
    public long getAccountId (@PathVariable long user_id)
    {
        return accountDao.getAccountID(user_id);
    }

}
