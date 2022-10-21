package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private static final String BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private final Scanner scanner = new Scanner(System.in);


    public void printGreeting()
    {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    //region MENUS
    public void printLoginMenu()
    {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu()
    {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void transferStatusUpdateMenu()
    {
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("---------");
    }
    //endregion

    //region PROMPTS
    public int promptForMenuSelection(String prompt)
    {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e)
        {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public UserCredentials promptForCredentials()
    {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt)
    {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt)
    {
        System.out.print(prompt);
        while (true)
        {
            try {
                return Integer.parseInt(scanner.nextLine());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt)
    {
        System.out.print(prompt);
        while (true)
        {
            try {
                return new BigDecimal(scanner.nextLine());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a decimal number.");
            }
        }
    }
    //endregion

    //region PRINT OUTS
    public void printUserList(List<User> usersList)
    {
        if (usersList != null) {
            System.out.println("-------------------------------------------");
            System.out.println("Users");
            System.out.println("ID \t\t Name");
            System.out.println("-------------------------------------------");
            for (User user : usersList)
            {
                System.out.println(user.getId() + "\t\t" + user.getUsername());
            }
            System.out.println("-------------------------------------------\n");
        }
    }

    public void printTransfers(Transfer[] transfers, long accountId)
    {
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("\nID\t\t\tFrom/To\t\t\tAmount");
        System.out.println("-------------------------------------------");

        String toAccount;
        String fromAccount;

        for (Transfer transfer : transfers)
        {
            System.out.print(transfer.getTransferId() + "\t\t");
            if (transfer.getTransferFromAcct() == accountId)
            {
                toAccount = restTemplate.exchange
                        (BASE_URL + "users/account/" + transfer.getTransferToAcct(), HttpMethod.GET, makeAuthenticatedEntity(), String.class).getBody();
                System.out.print("To: " + toAccount);
            }
            else
            {
                fromAccount = restTemplate.exchange
                        (BASE_URL + "users/account/" + transfer.getTransferFromAcct(), HttpMethod.GET, makeAuthenticatedEntity(), String.class).getBody();
                System.out.print("From: " + fromAccount);
            }
            System.out.println("\t\t$" + transfer.getTransferAmount());
        }
        System.out.println("-------------------------------------------");

    }

    public void printFullTransferDetails(int transferId, Transfer[] transfer)
    {
        //Get transfer ID.
        Transfer result = null;
        for (Transfer value : transfer)
        {
            if (value.getTransferId() == transferId)
                {result = value;}
        }
        if (result == null)
            {System.out.println("\nInvalid Transfer ID");}

        //Get transfer type.
        String transferType;
        if (result.getTransferType() == 1)
             {transferType = "Request";}
        else {transferType = "Send";}

        //Get transfer status.
        String transferStatus;
        if (result.getTransferStatus() == 1)
            {transferStatus = "Pending";}
        else if (result.getTransferStatus() == 2)
            {transferStatus = "Approved";}
        else
            {transferStatus = "Rejected";}

        //Print Out.
        System.out.println("--------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: " + result.getTransferId());

        System.out.println("From: " +
            restTemplate.exchange(BASE_URL + "users/account/" + result.getTransferFromAcct(), HttpMethod.GET,
            makeAuthenticatedEntity(),  String.class).getBody());

        System.out.println("To: " +
            restTemplate.exchange(BASE_URL + "users/account/" + result.getTransferToAcct(), HttpMethod.GET,
            makeAuthenticatedEntity(),  String.class).getBody());

        System.out.println("Type: " + transferType);
        System.out.println("Status: " + transferStatus);
        System.out.println("Amount: $" + result.getTransferAmount());
    }
    //endregion

    public void pause()
    {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {System.out.println("An error occurred. Check the log for details.");}


    @SuppressWarnings("rawtypes")
    public HttpEntity makeAuthenticatedEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(App.currentUserToken);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
