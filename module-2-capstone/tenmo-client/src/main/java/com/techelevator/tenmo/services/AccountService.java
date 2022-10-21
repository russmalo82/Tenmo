package com.techelevator.tenmo.services;


import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    public final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    public AuthenticatedUser currentUser;

    public AccountService(String url, AuthenticatedUser currentUser)
    {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public BigDecimal getBalance()
    {
        BigDecimal balance;
        balance = restTemplate.exchange
                (BASE_URL + "account/balance/" + App.currentUserId , HttpMethod.GET, makeAuthenticatedEntity(), BigDecimal.class).getBody();
        return balance;
    }

    public void decreaseBalance(long userId, BigDecimal amount)
    {
        Account account = new Account();
        //noinspection ConstantConditions
        long accountId = restTemplate.exchange(BASE_URL + "account/" + userId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();
        account.setAccountId(accountId);
        BigDecimal balance = restTemplate.exchange(BASE_URL + "account/balance/" + userId, HttpMethod.GET, makeAuthenticatedEntity(), BigDecimal.class).getBody();

        account.setBalance(balance.subtract(amount));
        account.setUserId(userId);

        restTemplate.exchange(BASE_URL + "account/" + userId, HttpMethod.PUT, makeAccountEntity(account), Account.class);
    }

    public void increaseBalance(long userId, BigDecimal amount)
    {
        Account account = new Account();
        //noinspection ConstantConditions
        long accountId = restTemplate.exchange(BASE_URL + "account/" + userId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();
        account.setAccountId(accountId);
        BigDecimal balance = restTemplate.exchange(BASE_URL + "account/balance/" + userId, HttpMethod.GET, makeAuthenticatedEntity(), BigDecimal.class).getBody();

        account.setBalance(balance.add(amount));
        account.setUserId(userId);

        restTemplate.exchange(BASE_URL + "account/" +userId, HttpMethod.PUT, makeAccountEntity(account), Account.class);
    }

    public HttpEntity makeAuthenticatedEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(App.currentUserToken);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

    public HttpEntity<Account> makeAccountEntity(Account account)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(App.currentUserToken);
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);
        return entity;
    }

}
