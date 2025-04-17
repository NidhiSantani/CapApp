package com.example.capstone_kitchen;

import com.google.firebase.firestore.DocumentReference;

public class Review {
    private String comment;
    private int rating;
    private DocumentReference foodItem;
    private String userId;
    private String cuisine;

    // Default constructor required for Firestore
    public Review() {}

    // Constructor
    public Review(String comment, int rating, DocumentReference foodItem, String userId, String cuisine) {
        this.comment = comment;
        this.rating = rating;
        this.foodItem = foodItem;
        this.userId = userId;
        this.cuisine = cuisine;
    }

    // Getters and Setters
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public DocumentReference getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(DocumentReference foodItem) {
        this.foodItem = foodItem;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
}