package com.example.capstone_kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_kitchen.WalletTransactionModel;

import java.util.List;

/**
 * Adapter to display a list of wallet transactions in a RecyclerView.
 */
public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.WalletViewHolder> {

    private final List<WalletTransactionModel> transactionList;

    public WalletHistoryAdapter(List<WalletTransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_history, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        WalletTransactionModel transaction = transactionList.get(position);

        // 1. Set the title and date/time
        holder.tvTransactionTitle.setText(transaction.getTitle());
        holder.tvTransactionDate.setText(transaction.getDateTime());

        // 2. Format the amount
        // If it's a credit, show "+Rs. amount" in green; if it's a debit, show "-Rs. amount" in red.
        double amount = transaction.getAmount();
        if (transaction.isCredit()) {
            // e.g., +Rs. 300
            holder.tvTransactionAmount.setText("+Rs. " + amount);
            holder.tvTransactionAmount.setTextColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark)
            );
            // Set the arrow icon to green or an up arrow
            holder.imgTransactionArrow.setImageResource(R.drawable.arrow_green_down);
        } else {
            // e.g., -Rs. 72
            holder.tvTransactionAmount.setText("-Rs. " + Math.abs(amount));
            holder.tvTransactionAmount.setTextColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark)
            );
            // Set the arrow icon to red or a down arrow if you prefer
            holder.imgTransactionArrow.setImageResource(R.drawable.arrow_red_up);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    /**
     * ViewHolder class that holds references to the views for each item.
     */
    static class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionTitle, tvTransactionDate, tvTransactionAmount;
        ImageView imgTransactionArrow;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionTitle  = itemView.findViewById(R.id.tvTransactionTitle);
            tvTransactionDate   = itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
            imgTransactionArrow = itemView.findViewById(R.id.imgTransactionArrow);
        }
    }
}