package com.example.capstone_kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.WalletViewHolder> {

    private final List<WalletTransactionModel> transactionList;

    public WalletHistoryAdapter(List<WalletTransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_history, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        WalletTransactionModel transaction = transactionList.get(position);

        holder.tvTransactionTitle.setText(transaction.getOrderId());
        holder.tvTransactionDate.setText(transaction.getDate());

        double amount = transaction.getAmount();
        if (transaction.getPaymentStatus().equals("Paid")) {
            holder.tvTransactionAmount.setText("- Rs. " + amount);
            holder.tvTransactionAmount.setTextColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark)
            );
            holder.imgTransactionArrow.setImageResource(R.drawable.arrow_red_up);
        } else {
            holder.tvTransactionAmount.setText("+ Rs. " + Math.abs(amount));
            holder.tvTransactionAmount.setTextColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark)
            );
            holder.imgTransactionArrow.setImageResource(R.drawable.arrow_green_down);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionTitle, tvTransactionDate, tvTransactionAmount;
        ImageView imgTransactionArrow;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionTitle  = itemView.findViewById(R.id.tvOrderId);
            tvTransactionDate   = itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
            imgTransactionArrow = itemView.findViewById(R.id.imgTransactionArrow);
        }
    }
}