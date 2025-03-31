package com.example.capstone_kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<FoodMenu> foodList;

    public FoodAdapter(List<FoodMenu> foodList) {
        this.foodList = foodList;
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
        holder.foodName.setText(food.getName());
        holder.foodDescription.setText(food.getDescription());
        holder.foodPrice.setText("₹ " + food.getPrice());
        holder.foodTime.setText(food.getTime());
        holder.foodImage.setImageResource(food.getImageResId());

        // Toggle heart icon
        holder.favIcon.setOnClickListener(v -> {
            boolean isFavorite = (holder.favIcon.getTag() != null && (boolean) holder.favIcon.getTag());
            if (isFavorite) {
                holder.favIcon.setImageResource(R.drawable.ic_heart_outline);
            } else {
                holder.favIcon.setImageResource(R.drawable.ic_heart_filled);
            }
            holder.favIcon.setTag(!isFavorite);
        });

        // Quantity adjustment
        holder.btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.quantityText.getText().toString());
            holder.quantityText.setText(String.valueOf(++quantity));
        });

        holder.btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.quantityText.getText().toString());
            if (quantity > 0) {
                holder.quantityText.setText(String.valueOf(--quantity));
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
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