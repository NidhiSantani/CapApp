package com.example.capstone_kitchen;

public class InvoiceOrderItem {
    private String itemName, estTime, counterNo, price;
    private int status; // 1: Ordered, 2: Preparing, 3: Collected
    private int imageResId;

    public InvoiceOrderItem(String itemName, String estTime, String counterNo, String price, int status, int imageResId) {
        this.itemName = itemName;
        this.estTime = estTime;
        this.counterNo = counterNo;
        this.price = price;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getItemName() { return itemName; }
    public String getEstTime() { return estTime; }
    public String getCounterNo() { return counterNo; }
    public String getPrice() { return price; }
    public int getStatus() { return status; }
    public int getImageResId() { return imageResId; }
}