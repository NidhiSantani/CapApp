package com.example.capstone_kitchen;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final List<OrderHistoryModel> orderList;

    public OrderHistoryAdapter(List<OrderHistoryModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the CardView layout for each order
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistoryModel order = orderList.get(position);
        Context context = holder.itemView.getContext();

        // 1. Set date/time and status
        holder.tvOrderDateTime.setText(order.getDate() + " | " + order.getTime());
        holder.tvOrderStatus.setText(order.getStatus());

        // 2. Update status icon based on status
        if ("Completed".equalsIgnoreCase(order.getStatus())) {
            holder.imgOrderStatus.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.imgOrderStatus.setImageResource(R.drawable.ic_close);
        }

        // 3. Clear any previous child views in llItemsContainer
        holder.llItemsContainer.removeAllViews();

        // 4. Dynamically create each order item view
        double total = 0.0;
        if (order.getItems() != null) {
            for (OrderItemModel item : order.getItems()) {
                // Create a horizontal LinearLayout for the row
                LinearLayout itemRow = new LinearLayout(context);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                itemRow.setLayoutParams(rowParams);
                itemRow.setOrientation(LinearLayout.HORIZONTAL);
                itemRow.setPadding(0, 8, 0, 8);

                // Create TextView for item details (quantity and name)
                TextView tvItemName = new TextView(context);
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                tvItemName.setLayoutParams(nameParams);
                tvItemName.setText(item.getQuantity() + " x " + item.getItemName());
                tvItemName.setTextSize(14);
                tvItemName.setTextColor(context.getResources().getColor(android.R.color.black));

                // Create TextView for the price
                TextView tvItemPrice = new TextView(context);
                LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvItemPrice.setLayoutParams(priceParams);
                double linePrice = item.getPrice() * item.getQuantity();
                tvItemPrice.setText("₹" + linePrice);
                tvItemPrice.setTextSize(14);
                tvItemPrice.setTextColor(context.getResources().getColor(android.R.color.black));
                tvItemPrice.setGravity(Gravity.END);

                // Add both TextViews to the horizontal layout
                itemRow.addView(tvItemName);
                itemRow.addView(tvItemPrice);

                // Add the row to the items container in the CardView
                holder.llItemsContainer.addView(itemRow);

                // Accumulate total
                total += linePrice;
            }
        }

        // 5. Show the total at the bottom of the card
        holder.tvItemTotal.setText("Item Total: ₹" + total);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder class that holds references to our views
    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderDateTime, tvOrderStatus, tvItemTotal;
        ImageView imgOrderStatus;
        LinearLayout llItemsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDateTime   = itemView.findViewById(R.id.tvOrderDateTime);
            tvOrderStatus     = itemView.findViewById(R.id.tvOrderStatus);
            tvItemTotal       = itemView.findViewById(R.id.tvItemTotal);
            imgOrderStatus    = itemView.findViewById(R.id.imgOrderStatus);
            llItemsContainer  = itemView.findViewById(R.id.llItemsContainer);
        }
    }
}
