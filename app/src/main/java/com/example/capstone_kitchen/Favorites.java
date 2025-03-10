package com.example.capstone_kitchen;
import com.example.capstone_kitchen.FavoriteAdapter;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<FoodItem> favoriteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.favorites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        favoriteList = new ArrayList<>();
        favoriteList.add(new FoodItem("Jumbo Vadapav", "Rs. 30.00", R.drawable.vadapav));
        favoriteList.add(new FoodItem("Jumbo Vadapav", "Rs. 30.00", R.drawable.vadapav));
        favoriteList.add(new FoodItem("Jumbo Vadapav", "Rs. 30.00", R.drawable.vadapav));
        favoriteList.add(new FoodItem("Jumbo Vadapav", "Rs. 30.00", R.drawable.vadapav));

        favoriteAdapter = new FavoriteAdapter(favoriteList, this);
        recyclerView.setAdapter(favoriteAdapter);


    }
}