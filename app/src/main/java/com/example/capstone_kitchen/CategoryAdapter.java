package com.example.capstone_kitchen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnItemClickListener listener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryImage.setImageResource(category.getImageResId());

        // Handle Click Event
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;
        CardView cardView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryImage = itemView.findViewById(R.id.category_image);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    // Interface for click handling
    public interface OnItemClickListener {
        void onItemClick(Category category);
    }
}