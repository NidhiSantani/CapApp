package com.example.capstone_kitchen;

/**
 * Represents a single wallet transaction, e.g., "Order Placed" or "Wallet top-up".
 */
public class WalletTransactionModel {

    private String title;       // e.g., "Order Placed" or "Wallet top-up via admin"
    private String dateTime;    // e.g., "February 28, 2025 | 13:26 PM"
    private double amount;      // e.g., -72.0 for an order, +300.0 for a top-up
    private boolean isCredit;   // true if it's a credit (top-up), false if it's a debit (order placed)

    public WalletTransactionModel(String title, String dateTime, double amount, boolean isCredit) {
        this.title = title;
        this.dateTime = dateTime;
        this.amount = amount;
        this.isCredit = isCredit;
    }

    // Getters and setters (if needed)
    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isCredit() {
        return isCredit;
    }
}