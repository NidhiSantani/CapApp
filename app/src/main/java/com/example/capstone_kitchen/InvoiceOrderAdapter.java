package com.example.capstone_kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InvoiceOrderAdapter extends RecyclerView.Adapter<InvoiceOrderAdapter.ViewHolder> {

    private List<InvoiceOrderItem> orderList;

    public InvoiceOrderAdapter(List<InvoiceOrderItem> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceOrderItem orderItem = orderList.get(position);
        holder.itemImage.setImageResource(orderItem.getImageResId());
        holder.itemName.setText(orderItem.getItemName());
        holder.itemPrice.setText("₹ " + orderItem.getPrice());
        holder.itemEstTime.setText(orderItem.getEstTime() + " min");
        holder.itemCounterNo.setText(orderItem.getCounterNo());

        // Update order status dynamically
        holder.status1.setAlpha(orderItem.getStatus() >= 1 ? 1f : 0.5f);
        holder.status2.setAlpha(orderItem.getStatus() >= 2 ? 1f : 0.5f);
        holder.status3.setAlpha(orderItem.getStatus() == 3 ? 1f : 0.5f);
        holder.status1.setBackgroundResource(orderItem.getStatus() == 1 ? R.drawable.circle_red : R.drawable.circle_gray);
        holder.status2.setBackgroundResource(orderItem.getStatus() == 2 ? R.drawable.circle_red : R.drawable.circle_gray);
        holder.status3.setBackgroundResource(orderItem.getStatus() == 3 ? R.drawable.circle_red : R.drawable.circle_gray);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice, itemEstTime, itemCounterNo;
        View status1, status2, status3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemEstTime = itemView.findViewById(R.id.itemEstTimeValue);
            itemCounterNo = itemView.findViewById(R.id.itemCounterNoValue);
            status1 = itemView.findViewById(R.id.status1);
            status2 = itemView.findViewById(R.id.status2);
            status3 = itemView.findViewById(R.id.status3);
        }
    }
}