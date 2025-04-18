package com.example.capstone_kitchen;

import java.util.Objects;

public class CartItem {
    private final String name;
    private final double price;
    private final String imageUrl;
    private int quantity;
    private final int estTime;  // Keep estTime as int

    // Constructor (accepting Long for estTime to handle Firestore data)
    public CartItem(String name, double price, String imageUrl, int quantity, int estTime) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.estTime = estTime;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getEstTime() {
        return estTime;
    }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Debug string
    @Override
    public String toString() {
        return "CartItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", estTime=" + estTime +
                '}';
    }

    // Equality check
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem cartItem = (CartItem) o;
        return Double.compare(cartItem.price, price) == 0 &&
                quantity == cartItem.quantity &&
                estTime == cartItem.estTime &&
                Objects.equals(name, cartItem.name) &&
                Objects.equals(imageUrl, cartItem.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, imageUrl, quantity, estTime);
    }
}