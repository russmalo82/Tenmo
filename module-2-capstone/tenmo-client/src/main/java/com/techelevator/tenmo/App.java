package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private AuthenticatedUser currentUser;

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL, currentUser);
    private final TransferService transferService = new TransferService(API_BASE_URL, currentUser);
    private final UserService userService = new UserService(API_BASE_URL, currentUser);

    public static String currentUserToken = "";
    public static String currentUserName = "";
    public static long currentUserId = 0;
    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args)
    {
        App app = new App();
        app.run();
    }

    private void run()
    {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null)
        {
            mainMenu();
        }
    }

    private void loginMenu()
    {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null)
        {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1)
            {
                handleRegister();
            }
            else if (menuSelection == 2)
            {
                handleLogin();
            }
            else if (menuSelection != 0)
            {
                System.out.println("Invalid Selection");
                //consoleService.pause();
            }
        }
    }

    private void handleRegister()
    {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials))
        {
            System.out.println("Registration successful. You can now login.");
        }
        else
        {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin()
    {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        try {
            if (currentUser != null)
            {
                currentUserToken = currentUser.getToken();
                currentUserName = currentUser.getUser().getUsername();
                currentUserId = currentUser.getUser().getId();
            }
            else
            {
                System.out.println("\nInvalid User Or Password");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    private void mainMenu()
    {
        int menuSelection = -1;
        while (true)
        {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1)
            {
                viewCurrentBalance();
            }
            else if (menuSelection == 2)
            {
                viewTransferHistory();
            }
            else if (menuSelection == 3)
            {
                viewPendingRequests();
            }
            else if (menuSelection == 4)
            {
                sendBucks();
            }
            else if (menuSelection == 5)
            {
                requestBucks();
            }
            else if (menuSelection == 0)
            {
                break;
            }
            else
            {
                System.out.println("Invalid Selection");
            }
            //consoleService.pause();
        }
    }

    private void viewCurrentBalance()
    {
        System.out.println("\nCurrent balance: $" + accountService.getBalance());
    }

    private void viewTransferHistory()
    {
        long accountId = userService.getAccountID(currentUserId);
        Transfer[] transfers = transferService.viewTransfers(accountId);
        consoleService.printTransfers(transfers, accountId);

        try {
            int transferID = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
            if(transferID != 0)
            {
                consoleService.printFullTransferDetails(transferID, transfers);
            }
            else{System.out.println("\nReturning to main menu...");}
        }
        catch (Exception e)
        {
            System.out.println("\nReturning to main menu...");
        }
    }

    private void viewPendingRequests()   //Pulls just list of transfers that have a "pending" status. This method also includes the code for updating the status of those transfers.
    {
        Transfer[] transfers;
        int approveOrDeny = -1;
        long requestedUserId;

        try {
            transfers = transferService.viewPendingTransfers(App.currentUserId);
            long accountId = userService.getAccountID(App.currentUserId);
            assert transfers != null;
            //Check if transfers array is empty.
            if (transfers.length!=0)
            {
                consoleService.printTransfers(transfers, accountId);

                long transferId = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
                if (transferId != 0)
                {
                    for (Transfer transfer : transfers)
                    {
                        if (transfer.getTransferToAcct() == accountId)
                        {
                            System.out.println("\nCan't approve or reject own request!");
                            System.out.println("\nReturning to main menu...");
                            return;
                        }
                    }
                }
                else
                {
                    System.out.println("\nReturning to main menu...");
                    return;
                }
                consoleService.transferStatusUpdateMenu();

                Transfer selectedTransfer = new Transfer();

                while (approveOrDeny < 0 || approveOrDeny > 2)
                {
                    approveOrDeny = consoleService.promptForInt("Please choose an option: ");
                }

                if (approveOrDeny == 1)
                {
                    for (Transfer transfer : transfers)
                    {
                        if (transfer.getTransferId() == transferId)
                        {
                            selectedTransfer = transfer;
                        }
                    }

                    //noinspection ConstantConditions
                    requestedUserId = restTemplate.exchange
                            (API_BASE_URL + "users/user_id/" + selectedTransfer.getTransferToAcct(), HttpMethod.GET, makeAuthenticatedEntity(), long.class).getBody();

                    boolean validUser = false;
                    List<User> users = userService.getAllUsers();
                    for (User user : users)
                    {
                        if (user.getId() == requestedUserId)
                        {
                            validUser = true;
                            break;
                        }
                    }
                    if (!validUser)
                    {
                        System.out.println("Invalid user!");
                    }

                    if(validUser){
                    //Check for sufficient balance.
                        if (userService.checkForTransferApproval(currentUserId,selectedTransfer.getTransferAmount()))
                        {
                            accountService.increaseBalance(requestedUserId, selectedTransfer.getTransferAmount());
                            accountService.decreaseBalance(App.currentUserId, selectedTransfer.getTransferAmount());
                            transferService.updateTransferStatus(transferId, 2);
                            System.out.println("\nTransfer accepted!");
                        }
                    }
                }

                else if (approveOrDeny == 2)
                {
                    transferService.updateTransferStatus(transferId, 3);
                    System.out.println("\nTransfer rejected!");
                }

                else {System.out.println("\nReturning to main menu...");}

            }
            else{System.out.println("You have no requests pending!");}
        }
        catch (NullPointerException e)
        {
            System.out.println("You have no requests pending!");
        }
    }

    private void sendBucks ()
    {
        consoleService.printUserList(userService.getAllUsers());
        int receiverId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if(receiverId!=0)
        {
            if (receiverId == currentUserId)
            {
                System.out.println("You cannot send bucks to yourself!");
                System.out.println("\nReturning to main menu...");
                return;
            }
            boolean validUser = false;
            List<User> users = userService.getAllUsers();
            for (User user : users)
            {
                if (user.getId() == receiverId)
                {
                    validUser = true;
                    break;
                }
            }
            if (!validUser)
            {
                System.out.println("Invalid user!");
            }

            if(validUser)
            {
                BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");

                //Process transfer if both parties are verified.
                if (userService.checkForTransferApproval(currentUserId, amount))
                {
                    transferService.sendTransfer(currentUserId, receiverId, amount);
                    accountService.decreaseBalance(currentUserId, amount);
                    accountService.increaseBalance(receiverId, amount);
                    System.out.println("\nTransaction successful!");
                }
                else
                {
                    System.out.println("Transfer not completed!");
                }
            }
        }
        else{System.out.println("\nReturning to main menu...");}
    }

    private void requestBucks () {

        consoleService.printUserList(userService.getAllUsers());

        int idToRequestFrom = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");

        if(idToRequestFrom!=0)
        {
            if (idToRequestFrom != currentUserId)
            {
                BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
                transferService.requestTransfer(idToRequestFrom, currentUserId, amount);
                System.out.println("\nTransfer requested successfully!");
            }
            else
            {
                System.out.println("\nYou cannot request bucks from yourself!");
                System.out.println("\nReturning to main menu...");
            }
        }
        else{System.out.println("\nReturning to main menu...");}
    }


    @SuppressWarnings("rawtypes")
    public HttpEntity makeAuthenticatedEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(App.currentUserToken);
    HttpEntity entity = new HttpEntity(headers);
    return entity;
    }
    }



