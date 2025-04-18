package com.example.capstone_kitchen;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FoodMenu {
    private String foodId;
    private String foodName;  // Changed to camelCase
    private String description;
    private double rate;
    private int makeTime;  // Changed to camelCase
    private String image;
    private boolean availabilityStatus;  // Changed to camelCase
    private int quantity; // New field for keeping track of quantity in order

    // Required empty constructor for Firestore deserialization
    public FoodMenu() {
        // Default constructor required by Firestore
    }

    // Updated constructor
    public FoodMenu(String foodId, String foodName, String description, double rate, int makeTime, String image, boolean availabilityStatus, int quantity) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.description = description;
        this.rate = rate;
        this.makeTime = makeTime;
        this.image = image;
        this.availabilityStatus = availabilityStatus;
        this.quantity = quantity; // Initialize the quantity
    }

    // Getters
    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {  // Updated getter
        return foodName;
    }

    public String getDescription() {
        return description;
    }

    public double getRate() {
        return rate;
    }

    public int getMakeTime() {  // Updated getter
        return makeTime;
    }

    public String getImage() {
        return image;
    }

    public boolean getAvailabilityStatus() {  // Updated getter
        return availabilityStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public void setFoodName(String foodName) {  // Updated setter
        this.foodName = foodName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setMakeTime(int makeTime) {  // Updated setter
        this.makeTime = makeTime;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {  // Updated setter
        this.availabilityStatus = availabilityStatus;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}