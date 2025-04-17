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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<FoodItem> favoriteList;
    private Context context;
    private String sapid;

    public FavoriteAdapter(List<FoodItem> favoriteList, Context context, String sapid) {
        this.favoriteList = favoriteList;
        this.context = context;
        this.sapid = sapid;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        FoodItem item = favoriteList.get(position);
        holder.foodName.setText(item.getName());
        holder.foodPrice.setText(item.getPrice());

        Glide.with(context).load(item.getImageUrl()).into(holder.foodImage);
        holder.heartIcon.setImageResource(R.drawable.ic_heart_filled); // Filled by default

        holder.heartIcon.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(sapid)
                    .update("favorites", com.google.firebase.firestore.FieldValue.arrayRemove(item.getId()))
                    .addOnSuccessListener(aVoid -> {
                        favoriteList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, favoriteList.size());
                    })
                    .addOnFailureListener(e -> Log.e("FavoriteAdapter", "Failed to remove from favorites", e));
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