package com.example.capstone_kitchen;

import java.util.List;

public class OrderHistoryModel {
    private String dateTime; // e.g., "April 17, 2025 03:45 PM"
    private String status;
    private List<OrderItemModel> items;

    public OrderHistoryModel(String dateTime, String status, List<OrderItemModel> items) {
        this.dateTime = dateTime;
        this.status = status;
        this.items = items;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItemModel> getItems() {
        return items;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItems(List<OrderItemModel> items) {
        this.items = items;
    }

    public double getTotal() {
        double sum = 0;
        for (OrderItemModel item : items) {
            sum += item.getPrice() * item.getQuantity();
        }
        return sum;
    }
}