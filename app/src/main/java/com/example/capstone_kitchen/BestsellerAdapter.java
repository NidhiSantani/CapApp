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
import android.util.Log;

public class BestsellerAdapter extends RecyclerView.Adapter<BestsellerAdapter.BestsellerViewHolder> {

    private Context context;
    private List<Bestseller> bestsellerList;

    public BestsellerAdapter(Context context, List<Bestseller> bestsellerList) {
        this.context = context;
        this.bestsellerList = bestsellerList;
    }

    @NonNull
    @Override
    public BestsellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bestseller_item, parent, false);
        return new BestsellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestsellerViewHolder holder, int position) {
        Bestseller bestseller = bestsellerList.get(position);
        holder.bestsellerName.setText(bestseller.getName());
        holder.bestsellerImage.setImageResource(bestseller.getImageResId());
        Log.d("BestsellerAdapter", "Name: " + bestseller.getName() + ", Image: " + bestseller.getImageResId());


        // Handle image loading dynamically from resource name
        int imageResId = bestseller.getImageResId();
        if (imageResId != 0) {
            holder.bestsellerImage.setImageResource(R.drawable.foodph);
        }
    }

    @Override
    public int getItemCount() {
        return bestsellerList.size();
    }

    static class BestsellerViewHolder extends RecyclerView.ViewHolder {
        TextView bestsellerName;
        ImageView bestsellerImage;
        CardView cardView;

        BestsellerViewHolder(View itemView) {
            super(itemView);
            bestsellerName = itemView.findViewById(R.id.bestseller_name);
            bestsellerImage = itemView.findViewById(R.id.bestseller_image);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}