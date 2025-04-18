package com.example.capstone_kitchen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItem> cartList;

    public CartAdapter(Context context, List<CartItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(String.format("₹ %.2f", item.getPrice()));
        holder.itemQuantity.setText(String.format("Qty: %d", item.getQuantity()));
        holder.itemEstTime.setText(String.format("Est.: %d min", item.getEstTime()));

        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {  // More robust check for non-empty imageUrl
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.foodph)  // Default image while loading
                    .error(R.drawable.foodph)  // Error image if the URL fails to load
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.foodph);  // Set default image if no URL
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemQuantity, itemEstTime;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemImage = itemView.findViewById(R.id.item_image);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemEstTime = itemView.findViewById(R.id.estimated_time);
        }
    }
}