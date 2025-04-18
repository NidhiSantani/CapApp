package com.example.capstone_kitchen;

// Category model to hold name and image URL
public class Category {
    private String name;
    private String imageUrl;
    private String cuisineId;

    public Category(String name, String imageUrl, String cuisineId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.cuisineId = cuisineId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCuisineId() {
        return cuisineId;
    }
}