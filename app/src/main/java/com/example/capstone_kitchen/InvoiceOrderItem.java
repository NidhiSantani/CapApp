package com.example.capstone_kitchen;

public class InvoiceOrderItem {
    private String itemName;
    private String estTime;
    private String counterNo;
    private String imageUrl;
    private double price;
    private double rate; // New field for rate
    private int status;
    private int quantity;

    // Constructor with rate included
    public InvoiceOrderItem(String itemName, String estTime, String counterNo, double price, int status, String imageUrl, int quantity, double rate) {
        this.itemName = itemName;
        this.estTime = estTime;
        this.counterNo = counterNo;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.rate = rate;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getEstTime() {
        return estTime;
    }

    public void setEstTime(String estTime) {
        this.estTime = estTime;
    }

    public String getCounterNo() {
        return counterNo;
    }

    public void setCounterNo(String counterNo) {
        this.counterNo = counterNo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    // Calculate total price based on rate and quantity
    public double getTotalPrice() {
        return rate * quantity;
    }
}