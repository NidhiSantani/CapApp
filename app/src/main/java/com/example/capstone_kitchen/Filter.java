package com.example.capstone_kitchen;

public class Filter {
    private String name;
    private int imageResId;
    private boolean isSelected;

    public Filter(String name, int imageResId, boolean isSelected) {
        this.name = name;
        this.imageResId = imageResId;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // ✅ Add toggle method for cleaner state updates
    public void toggleSelected() {
        this.isSelected = !this.isSelected;
    }
}