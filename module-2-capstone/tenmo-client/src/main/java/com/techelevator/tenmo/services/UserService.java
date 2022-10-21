package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class UserService {

    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    public AuthenticatedUser currentUser;

    public UserService(String url, AuthenticatedUser currentUser)
    {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public long getAccountID(long user_id)
    {
        //noinspection ConstantConditions
        return restTemplate.exchange
                (BASE_URL + "account/" + user_id, HttpMethod.GET, makeAuthenticatedEntity(), Long.class).getBody();
    }

    public List<User> getAllUsers()
    {
        User[] userArray = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthenticatedEntity(), User[].class).getBody();
        assert userArray != null;
        return Arrays.asList(userArray);
    }

    //This is to verify balances can allow a transfer
    public boolean checkForTransferApproval(long senderId, BigDecimal amount)
    {
        boolean cleared = false;
        BigDecimal senderBalance = restTemplate.exchange
                (BASE_URL + "account/balance/" + senderId, HttpMethod.GET, makeAuthenticatedEntity(), BigDecimal.class).getBody();
        if (amount.compareTo(senderBalance) <=0)
        {
            cleared = true;
        }
        else
        {
            System.out.println("\nInsufficient funds!");
        }
        return cleared;
    }

    @SuppressWarnings("rawtypes")
    public HttpEntity makeAuthenticatedEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(App.currentUserToken);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
