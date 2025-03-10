package com.example.capstone_kitchen;

public class CartItem {
    private String name;
    private double price;
    private int imageResId;
    private int quantity;

    public CartItem(String name, int price, int imageResId, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public int getQuantity() { return quantity; }
}