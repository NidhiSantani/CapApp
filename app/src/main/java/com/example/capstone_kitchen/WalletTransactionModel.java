package com.example.capstone_kitchen;

public class WalletTransactionModel {

    private String orderId;
    private String date;
    private double amount;
    private String paymentStatus;

    public WalletTransactionModel(String orderId, String date, double amount, String paymentStatus) {
        this.orderId = orderId;
        this.date = date;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}