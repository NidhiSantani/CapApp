package com.example.capstone_kitchen;

import java.util.Date;

public class PaymentRecord {
    private double amount;
    private Date date;
    private String mode;
    private String order_id;
    private String user_id;
    private String payment_status;

    public PaymentRecord(double amount, Date date, String mode, String order_id, String user_id, String payment_status) {
        this.amount = amount;
        this.date = date;
        this.mode = mode;
        this.order_id = order_id;
        this.user_id = user_id;
        this.payment_status = payment_status;
    }

    // Getters and setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }
}
