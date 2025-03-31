package com.example.capstone_kitchen;

public class Bestseller {
    private String name;
    private int imageResId;

    public Bestseller(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}