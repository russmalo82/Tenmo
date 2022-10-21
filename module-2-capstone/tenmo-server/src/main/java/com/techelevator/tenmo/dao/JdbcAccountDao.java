package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @Override
    public BigDecimal getBalance(long user_id)
    {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        BigDecimal balance = new BigDecimal(0);
        try {
                balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, user_id);
        }
        catch (DataAccessException e)
        {
            System.out.println("Error Getting Balance.");
        }
        return balance;
        }

    @Override
    public void updateAccount(Account account, long user_id)
    {
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, account.getBalance(), user_id);
    }

    @Override
    public long getAccountID(long user_id)  // Grabs account ID by user ID
    {
        String sql = "SELECT account_id FROM account WHERE user_id = ?";

        //noinspection ConstantConditions
        return jdbcTemplate.queryForObject(sql, long.class, user_id);
    }

    @Override
    public Account getAccountByUserId(long user_id)   //Grabs account details by user ID
    {
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);
        if (results.next())
        {
            return mapRowToAccount(results);
        }
        return null;
    }

    private Account mapRowToAccount(SqlRowSet rs)
    {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }

}
