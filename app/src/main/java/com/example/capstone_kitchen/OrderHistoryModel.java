package com.example.capstone_kitchen;

import java.util.List;

public class OrderHistoryModel {
    private String date;       // e.g., "February 28, 2025"
    private String time;       // e.g., "13:26 PM"
    private String status;     // e.g., "Completed"
    private List<OrderItemModel> items;

    public OrderHistoryModel(String date, String time, String status, List<OrderItemModel> items) {
        this.date = date;
        this.time = time;
        this.status = status;
        this.items = items;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItemModel> getItems() {
        return items;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
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
