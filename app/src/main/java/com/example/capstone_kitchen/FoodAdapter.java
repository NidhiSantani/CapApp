package com.example.capstone_kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<FoodMenu> foodList;
    private String sapid;
    private List<String> userFavorites;
    private OrderModel orderModel;
    private FirebaseFirestore db;

    public FoodAdapter(List<FoodMenu> foodList, String sapid, List<String> userFavorites, OrderModel orderModel) {
        this.foodList = foodList;
        this.sapid = sapid;
        this.userFavorites = userFavorites;
        this.orderModel = orderModel;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_food_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodMenu food = foodList.get(position);
        String foodId = food.getFoodId();

        // Set food name, description, price, and time
        holder.foodName.setText(food.getFoodName());
        holder.foodDescription.setText(food.getDescription());
        holder.foodPrice.setText("₹ " + String.format("%.2f", food.getRate()));
        holder.foodTime.setText(food.getMakeTime() + " mins");

        Glide.with(holder.foodImage.getContext())
                .load(food.getImage())
                .placeholder(R.drawable.foodph)
                .into(holder.foodImage);

        // Availability
        boolean isAvailable = food.getAvailabilityStatus();
        holder.itemView.setAlpha(isAvailable ? 1.0f : 0.5f);
        holder.itemView.setEnabled(isAvailable);
        holder.favIcon.setEnabled(isAvailable);
        holder.btnPlus.setEnabled(isAvailable);
        holder.btnMinus.setEnabled(isAvailable);

        // Quantity from Firestore (orderModel)
        orderModel.getItemQuantity(foodId, quantity -> holder.quantityText.setText(String.valueOf(quantity)));

        // Set favorite icon based on userFavorites
        boolean isFavorite = userFavorites.contains(foodId);
        holder.favIcon.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        holder.favIcon.setTag(isFavorite);

        holder.favIcon.setOnClickListener(v -> {
            boolean currentStatus = (boolean) holder.favIcon.getTag();

            // Limit to 4 favorites
            if (!currentStatus && userFavorites.size() >= 4) {
                Toast.makeText(v.getContext(), "You can only have 4 favorites !!", Toast.LENGTH_SHORT).show();
                return;
            }

            holder.favIcon.setEnabled(false); // Prevent rapid taps

            if (currentStatus) {
                // Remove from favorites
                db.collection("user").document(sapid)
                        .update("favorites", FieldValue.arrayRemove(foodId))
                        .addOnSuccessListener(unused -> {
                            userFavorites.remove(foodId);
                            holder.favIcon.setImageResource(R.drawable.ic_heart_outline);
                            holder.favIcon.setTag(false);
                            holder.favIcon.setEnabled(true);
                        });
            } else {
                // Add to favorites
                db.collection("user").document(sapid)
                        .update("favorites", FieldValue.arrayUnion(foodId))
                        .addOnSuccessListener(unused -> {
                            userFavorites.add(foodId);
                            holder.favIcon.setImageResource(R.drawable.ic_heart_filled);
                            holder.favIcon.setTag(true);
                            holder.favIcon.setEnabled(true);
                        });
            }
        });

        // Quantity + button
        holder.btnPlus.setOnClickListener(v -> {
            orderModel.getItemQuantity(foodId, quantity -> {
                int newQuantity = quantity + 1;
                holder.quantityText.setText(String.valueOf(newQuantity));
                orderModel.addFoodToOrder(foodId, 1, food.getRate(), food.getMakeTime());
            });
        });

        // Quantity - button
        holder.btnMinus.setOnClickListener(v -> {
            orderModel.getItemQuantity(foodId, quantity -> {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    holder.quantityText.setText(String.valueOf(newQuantity));
                    orderModel.updateOrderItemQuantity(foodId, newQuantity);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // 🔧 ✅ New method to update favorites
    public void updateFavorites(List<String> newFavorites) {
        this.userFavorites = newFavorites;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodDescription, foodPrice, foodTime, quantityText;
        ImageView foodImage, favIcon;
        TextView btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodDescription = itemView.findViewById(R.id.food_description);
            foodPrice = itemView.findViewById(R.id.food_price);
            foodTime = itemView.findViewById(R.id.food_time);
            foodImage = itemView.findViewById(R.id.food_image);
            favIcon = itemView.findViewById(R.id.fav_icon);
            quantityText = itemView.findViewById(R.id.quantity_text);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
        }
    }
}