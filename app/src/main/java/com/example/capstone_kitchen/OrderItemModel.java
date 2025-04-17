package com.example.capstone_kitchen;

public class OrderItemModel {
    private String foodName; // Name of the item
    private int quantity;    // Quantity of the item
    private double price;    // Price of the item
    private String foodId;   // Food ID (to fetch food item details from Firestore)

    // Constructor
    public OrderItemModel(String foodName, int quantity, double price, String foodId) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
        this.foodId = foodId;
    }

    // Getters
    public String getFoodName() {
        return foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getFoodId() {
        return foodId;
    }

    // Setters
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
}