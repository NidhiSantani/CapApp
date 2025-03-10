package com.example.capstone_kitchen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<FoodItem> favoriteList;
    private Context context;

    public FavoriteAdapter(List<FoodItem> favoriteList, Context context) {
        this.favoriteList = favoriteList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = favoriteList.get(position);
        holder.foodName.setText(item.getName());
        holder.foodPrice.setText(item.getPrice());
        holder.foodImage.setImageResource(item.getImageResId());

        // Remove item when clicking heart icon
        holder.heartIcon.setOnClickListener(v -> {
            favoriteList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteList.size());
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage, heartIcon;
        TextView foodName, foodPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            heartIcon = itemView.findViewById(R.id.heartIcon);
        }
    }
}
