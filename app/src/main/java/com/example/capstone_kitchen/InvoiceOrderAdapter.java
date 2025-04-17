package com.example.capstone_kitchen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.List;

public class InvoiceOrderAdapter extends RecyclerView.Adapter<InvoiceOrderAdapter.ViewHolder> {

    private static final String TAG = "InvoiceOrderAdapter";

    private List<InvoiceOrderItem> orderList;
    private Context context;

    // Constructor
    public InvoiceOrderAdapter(Context context, List<InvoiceOrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceOrderItem orderItem = orderList.get(position);

        // Log statement to confirm data binding
        Log.d(TAG, "Binding item: " + orderItem.getItemName()
                + " | Qty: " + orderItem.getQuantity()
                + " | Price: ₹" + orderItem.getPrice());

        // Load image using Glide
        Glide.with(context)
                .load(orderItem.getImageUrl())
                .placeholder(R.drawable.foodph)  // Placeholder in case image is loading
                .error(R.drawable.foodph)  // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache the image for better performance
                .into(holder.itemImage);

        // Set item name
        holder.itemName.setText(orderItem.getItemName());

        // Calculate and set the total price based on quantity and rate
        double totalPrice = orderItem.getPrice() * orderItem.getQuantity();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        holder.itemPrice.setText("₹ " + decimalFormat.format(totalPrice));

        // Set estimated time for food item
        holder.itemEstTime.setText(orderItem.getEstTime() + " min");

        // Set counter number for the food item
        holder.itemCounterNo.setText(orderItem.getCounterNo());

        // Set quantity
        holder.quantityValue.setText(String.valueOf(orderItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        // Return the size of the order list
        return orderList != null ? orderList.size() : 0;
    }

    // ViewHolder class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;  // Image of the food item
        TextView itemName, itemPrice, itemEstTime, itemCounterNo, quantityValue;  // Other details of the food item

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);  // Image view for the item image
            itemName = itemView.findViewById(R.id.itemName);  // Text view for the item name
            itemPrice = itemView.findViewById(R.id.itemPrice);  // Text view for the item price
            itemEstTime = itemView.findViewById(R.id.itemEstTimeValue);  // Text view for the estimated time
            itemCounterNo = itemView.findViewById(R.id.itemCounterNoValue);  // Text view for the counter number
            quantityValue = itemView.findViewById(R.id.quantityvalue);  // Text view for the quantity
        }
    }

    // Method to update the adapter with a new list of order items
    public void updateOrderList(List<InvoiceOrderItem> newOrderList) {
        if (newOrderList != null) {
            this.orderList = newOrderList;  // Update the order list
            Log.d(TAG, "Updated order list received with size: " + newOrderList.size());
            notifyDataSetChanged();  // Notify the adapter to refresh the data
        } else {
            Log.e(TAG, "Received null order list for update");
        }
    }

    // Add any utility methods or setters if needed, based on your app's requirements
}