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

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<Filter> filterList;
    private Context context;

    public FilterAdapter(List<Filter> filterList) {
        this.filterList = filterList;
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
        holder.filterImage.setImageResource(filter.getImageResId());

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
}