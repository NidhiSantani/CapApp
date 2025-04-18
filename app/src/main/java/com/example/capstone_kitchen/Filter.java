package com.example.capstone_kitchen;

public class Filter {
    private String name;
    private String imageUrl;
    private boolean isSelected;
    private String cuisineId; // Add this field

    // Modify the constructor to accept cuisineId
    public Filter(String name, String imageUrl, boolean isSelected, String cuisineId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
        this.cuisineId = cuisineId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void toggleSelected() {
        this.isSelected = !this.isSelected;
    }

    // Getter for cuisineId
    public String getCuisineId() {
        return cuisineId;
    }
}