package com.anola.t_bank.model;

public class TransactionModel {

    private String mydate;
    private String transactionType;
    private String amount;

    public TransactionModel() {
    }

    public TransactionModel(String mydate, String transactionType, String amount) {
        this.mydate = mydate;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public String getMydate() {
        return mydate;
    }

    public void setMydate(String mydate) {
        this.mydate = mydate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "mydate='" + mydate + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
