package com.example.capstone_kitchen;

public class FoodMenu {
    private String name;
    private String description;
    private String price;
    private String time;
    private int imageResId;

    public FoodMenu(String name, String description, String price, String time, int imageResId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.time = time;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public int getImageResId() {
        return imageResId;
    }
}