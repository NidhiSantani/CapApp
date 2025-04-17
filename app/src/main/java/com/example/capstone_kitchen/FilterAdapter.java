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

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<Filter> filterList;
    private Context context;
    private OnFilterClickListener onFilterClickListener;

    public FilterAdapter(Context context, List<Filter> filterList, OnFilterClickListener onFilterClickListener) {
        this.context = context;
        this.filterList = filterList;
        this.onFilterClickListener = onFilterClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Filter filter = filterList.get(position);
        holder.filterName.setText(filter.getName());

        String imageUrl = filter.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.filterImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.foodph)
                    .into(holder.filterImage);
        }

        // Set the click listener on the filter item
        holder.itemView.setOnClickListener(v -> {
            // Notify the activity about the filter click
            if (onFilterClickListener != null) {
                onFilterClickListener.onFilterClick(filter.getCuisineId()); // Passing the cuisineId
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView filterName;
        ImageView filterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
            filterImage = itemView.findViewById(R.id.filter_image);
        }
    }

    // Interface to handle filter click event
    public interface OnFilterClickListener {
        void onFilterClick(String cuisineId);
    }
}