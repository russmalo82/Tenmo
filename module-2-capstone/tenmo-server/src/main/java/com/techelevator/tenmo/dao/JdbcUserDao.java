package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long findIdByUsername(String username) {  //Grabs user ID from username
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, username);
        if (id != null)
        {
            return id;
        }
        else
        {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {  //Pulls a list of all users
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next())
        {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {  //Grabs full USER details by username
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next())
        {
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }


    @Override
    public boolean create(String username, String password) {  // -- create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        }
        catch (DataAccessException e)
        {
            return false;
        }
        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        }
        catch (DataAccessException e)
        {
            return false;
        }
        return true;
    }

    @Override
    public User getUserByID(long user_id) {  //Grab individual user by user ID
        User user = null;
        String sql = "SELECT * FROM tenmo_user WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);
        if (results.next())
        {
            user = mapRowToUser(results);
        }
        return user;
    }

    @Override
    public String getUsernameByAccountId(long accountId) {  //Grabs username by Account ID
        String username;
        String sql =
                "SELECT username FROM tenmo_user " +
                "JOIN account ON tenmo_user.user_id = account.user_id " +
                "WHERE account.account_id = ?;";
        username = jdbcTemplate.queryForObject(sql, String.class, accountId);
        return username;
    }

    @Override
    public long getUserIdByAccountId(long accountId) {  //Grabs full User by Account ID
        long userId;
        String sql = "Select user_id FROM account WHERE account_id = ?;";
        //noinspection ConstantConditions
        userId = jdbcTemplate.queryForObject(sql, Long.class, accountId);
        return userId;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
