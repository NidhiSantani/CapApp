package com.example.capstone_kitchen;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final List<OrderHistoryModel> orderList;

    public OrderHistoryAdapter(List<OrderHistoryModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistoryModel order = orderList.get(position);
        Context context = holder.itemView.getContext();

        // Set dateTime and status
        holder.tvOrderDateTime.setText(order.getDateTime());
        holder.tvOrderStatus.setText(order.getStatus());

        // Update status icon
        if ("Cancelled".equalsIgnoreCase(order.getStatus())) {
            holder.imgOrderStatus.setImageResource(R.drawable.ic_close);
        } else {
            holder.imgOrderStatus.setImageResource(R.drawable.ic_check_circle);
        }

        // Clear old views
        holder.llItemsContainer.removeAllViews();

        // Add item views
        final AtomicInteger total = new AtomicInteger(0); // Use AtomicInteger for mutable integer value

        if (order.getItems() != null) {
            // Use a counter to track when all items are processed
            final int[] processedItems = {0};

            for (OrderItemModel item : order.getItems()) {
                LinearLayout itemRow = new LinearLayout(context);
                itemRow.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                itemRow.setOrientation(LinearLayout.HORIZONTAL);
                itemRow.setPadding(0, 8, 0, 8);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("food_item")
                        .document(item.getFoodId())  // Fetch food item using food_id
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    String foodName = snapshot.getString("food_name");
                                    Log.d("Fetched food item", "Food ID: " + item.getFoodId() + ", Food name: " + foodName);  // Log food_id and food name

                                    // Item name
                                    TextView tvItemName = new TextView(context);
                                    tvItemName.setLayoutParams(new LinearLayout.LayoutParams(
                                            0,
                                            ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                                    tvItemName.setText(item.getQuantity() + " x " + (foodName != null ? foodName : "Unknown"));
                                    tvItemName.setTextSize(14);
                                    tvItemName.setTextColor(context.getResources().getColor(android.R.color.black));

                                    // Item price
                                    TextView tvItemPrice = new TextView(context);
                                    tvItemPrice.setLayoutParams(new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                                    double linePrice = item.getPrice() * item.getQuantity();
                                    tvItemPrice.setText("₹" + linePrice);
                                    tvItemPrice.setTextSize(14);
                                    tvItemPrice.setTextColor(context.getResources().getColor(android.R.color.black));
                                    tvItemPrice.setGravity(Gravity.END);

                                    itemRow.addView(tvItemName);
                                    itemRow.addView(tvItemPrice);
                                    holder.llItemsContainer.addView(itemRow);

                                    total.addAndGet((int) linePrice);  // Use addAndGet to modify the total
                                } else {
                                    Log.d("Fetched food item", "Document not found for food_id: " + item.getFoodId());  // Document not found
                                }
                            } else {
                                Log.d("Fetched food item", "Error fetching document: " + task.getException());  // Log the error
                            }

                            // Track when all items are processed
                            processedItems[0]++;

                            // After processing all items, update the total
                            if (processedItems[0] == order.getItems().size()) {
                                holder.tvItemTotal.setText("Item Total: ₹" + total.get());
                            }
                        });

            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDateTime, tvOrderStatus, tvItemTotal;
        ImageView imgOrderStatus;
        LinearLayout llItemsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDateTime = itemView.findViewById(R.id.tvOrderDateTime);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            imgOrderStatus = itemView.findViewById(R.id.imgOrderStatus);
            llItemsContainer = itemView.findViewById(R.id.llItemsContainer);
        }
    }
}