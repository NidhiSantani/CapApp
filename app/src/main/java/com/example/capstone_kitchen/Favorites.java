package com.example.capstone_kitchen;
import com.example.capstone_kitchen.FavoriteAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Favorites.this, HomePage.class));
            }
        });

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(Favorites.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(Favorites.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(Favorites.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(Favorites.this, Cart.class));
                    return true;
                }

                return false;
            }
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