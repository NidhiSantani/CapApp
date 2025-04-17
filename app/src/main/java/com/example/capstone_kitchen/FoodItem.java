package com.example.capstone_kitchen;

public class FoodItem {
    private String id;
    private String name;
    private String price;
    private String imageUrl;

    public FoodItem(String id, String name, String price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}