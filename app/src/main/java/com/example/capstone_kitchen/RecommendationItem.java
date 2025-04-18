package com.example.capstone_kitchen;

public class RecommendationItem {
    private String name;
    private double price;
    private String imageUrl;

    public RecommendationItem(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}