package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    public AuthenticatedUser currentUser;

    public TransferService(String url, AuthenticatedUser currentUser)
    {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public Transfer[] viewTransfers(long accountId)
    {
        Transfer[] transfers;
        transfers = restTemplate.exchange
                (BASE_URL + "transfer/" + accountId, HttpMethod.GET, makeAuthenticatedEntity(), Transfer[].class).getBody();
        return transfers;
    }

    public Transfer[] viewPendingTransfers(long userId)
    {
        //noinspection ConstantConditions
        long accountId = restTemplate.exchange(BASE_URL + "account/" + userId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();

        Transfer[] transfers;
        transfers = restTemplate.exchange(BASE_URL + "transfer/pending/" + accountId, HttpMethod.GET, makeAuthenticatedEntity(), Transfer[].class).getBody();
        return transfers;
    }

    @SuppressWarnings("ConstantConditions")
    public void sendTransfer(long fromUserId, long toUserId, BigDecimal amount)
    {
        try {
            long fromAcct = restTemplate.exchange(BASE_URL + "account/" + fromUserId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();
            long toAcct = restTemplate.exchange(BASE_URL + "account/" + toUserId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();

            Transfer transfer = new Transfer();
            transfer.setTransferType(2);
            transfer.setTransferStatus(2);
            transfer.setTransferFromAcct(fromAcct);
            transfer.setTransferToAcct(toAcct);
            transfer.setTransferAmount(amount);

            restTemplate.exchange(BASE_URL + "transfer/", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void requestTransfer(long fromUserId, long toUserId, BigDecimal amount)
    {
        try {
            long fromAcct = restTemplate.exchange(BASE_URL + "account/" + fromUserId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();
            long toAcct = restTemplate.exchange(BASE_URL + "account/" + toUserId, HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();

            Transfer transfer = new Transfer();
            transfer.setTransferType(1);
            transfer.setTransferStatus(1);
            transfer.setTransferFromAcct(fromAcct);
            transfer.setTransferToAcct(toAcct);
            transfer.setTransferAmount(amount);

            restTemplate.exchange(BASE_URL + "transfer/", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void updateTransferStatus(long transferId, int newStatus)
    {
        restTemplate.exchange
            (BASE_URL + "transfer/status/" + transferId + "/" + newStatus, HttpMethod.PUT, makeAuthenticatedEntity(), int.class);
    }

    @SuppressWarnings("rawtypes")
    public HttpEntity makeAuthenticatedEntity()
    {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(App.currentUserToken);
    HttpEntity entity = new HttpEntity(headers);
    return entity;
    }

    public HttpEntity<Transfer> makeTransferEntity(Transfer transfer)
    {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(App.currentUserToken);
    HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
    return entity;
    }
}
