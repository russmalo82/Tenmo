package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

    private JdbcUserDao userDao;
    public UserController(JdbcUserDao userDao) {this.userDao = userDao;}

    @RequestMapping(path = "users/{user_id}", method = RequestMethod.GET)
    public User getUserByID(@PathVariable long user_id)
    {
        return userDao.getUserByID(user_id);
    }

    @RequestMapping(path = "users/names/{username}", method = RequestMethod.GET)
    public User getUserByName(@PathVariable String username)
    {
        return userDao.findByUsername(username);
    }

    @RequestMapping(path = "users/account/{account_id}", method = RequestMethod.GET)
    public String getUsernameByAccountId(@PathVariable long account_id)
    {
        return userDao.getUsernameByAccountId(account_id);
    }

    @RequestMapping(path = "users/user_id/{account_id}", method = RequestMethod.GET)
    public long getUserIdByAccountId(@PathVariable long account_id)
    {
        return userDao.getUserIdByAccountId(account_id) ;
    }

    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User>findAll() {return userDao.findAll();}
}

