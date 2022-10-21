package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private JdbcTransferDao transferDao;

    public TransferController(JdbcTransferDao transferDao) {this.transferDao = transferDao;}


    @RequestMapping(path = "transfer/{account_id}" , method = RequestMethod.GET)
    public List<Transfer> listMyTransfers(@PathVariable long account_id)
    {
        return transferDao.getMyTransferList(account_id);
    }

    @RequestMapping(path = "transfer/pending/{account_id}", method = RequestMethod.GET)
    public List<Transfer> listMyPendingTransfers(@PathVariable long account_id)
    {
       return transferDao.getMyPendingTransfers(account_id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "transfer/", method = RequestMethod.POST)
    public void createTransfer(@RequestBody Transfer transfer) {
        transferDao.createTransfer(transfer);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "transfer/status/{transfer_id}/{status_id}", method = RequestMethod.PUT)
    public void updateTransferStatus(@PathVariable long transfer_id, @PathVariable int status_id)
    {
        transferDao.updateTransferStatus(transfer_id, status_id);
    }

}
