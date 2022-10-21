package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao
{
    List<Transfer> getMyTransferList(long accountId);

    List<Transfer> getMyPendingTransfers(long accountId);

    Transfer getTransferById(long transferId);

    void createTransfer(Transfer transfer);

    void updateTransferStatus(long transferId, int transferStatus);
}