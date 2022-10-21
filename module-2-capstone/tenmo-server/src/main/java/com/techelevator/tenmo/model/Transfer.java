package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private long transferId;
    private BigDecimal transferAmount;
    private long transferToAcct;
    private long transferFromAcct;
    private int transferType;
    private int transferStatus;

    public long getTransferId() {return transferId;}

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public long getTransferToAcct() {
        return transferToAcct;
    }

    public void setTransferToAcct(long transferToAcct) {
        this.transferToAcct = transferToAcct;
    }

    public long getTransferFromAcct() {
        return transferFromAcct;
    }

    public void setTransferFromAcct(long transferFromAcct) {
        this.transferFromAcct = transferFromAcct;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferAmount=" + transferAmount +
                ", transferToAcct=" + transferToAcct +
                ", transferFromAcct=" + transferFromAcct +
                ", transferType=" + transferType +
                ", transferStatus=" + transferStatus +
                '}';
    }


}
