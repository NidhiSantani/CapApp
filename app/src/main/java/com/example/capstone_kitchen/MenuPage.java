package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MenuPage extends AppCompatActivity {

    private RecyclerView filterRecycler;
    private FilterAdapter filterAdapter;
    private List<Filter> filterList;

    private RecyclerView foodRecycler;
    private FoodAdapter foodAdapter;
    private List<FoodMenu> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        // Initialize RecyclerView for filters
        filterRecycler = findViewById(R.id.filter_recycler);
        filterRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadFilters();
        filterAdapter = new FilterAdapter(filterList);
        filterRecycler.setAdapter(filterAdapter);

        // Initialize RecyclerView for food items
        foodRecycler = findViewById(R.id.food_recycler);
        foodRecycler.setLayoutManager(new LinearLayoutManager(this));
        loadFoodItems();
        foodAdapter = new FoodAdapter(foodList);
        foodRecycler.setAdapter(foodAdapter);


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(MenuPage.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(MenuPage.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(MenuPage.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(MenuPage.this, Cart.class));
                    return true;
                }

                return false;
            }
        });


    }

    // Load sample filter data
    private void loadFilters() {
        filterList = new ArrayList<>();
        filterList.add(new Filter("Chaat", R.drawable.vadapav, false));
        filterList.add(new Filter("Desserts", R.drawable.vadapav, false));
        filterList.add(new Filter("Juices", R.drawable.vadapav, false));
        filterList.add(new Filter("Pasta", R.drawable.vadapav, false));
        filterList.add(new Filter("Maggie", R.drawable.vadapav, false));
        filterList.add(new Filter("Pizza", R.drawable.vadapav, false));
    }

    // Load sample food data
    private void loadFoodItems() {
        foodList = new ArrayList<>();
        foodList.add(new FoodMenu("Jumbo Vadapav", "Relishing combo of bread & potato", "50", "2 mins", R.drawable.sample_food));
        foodList.add(new FoodMenu("Paneer Sandwich", "Delicious paneer with fresh veggies", "70", "5 mins", R.drawable.sample_food));
        foodList.add(new FoodMenu("Masala Dosa", "Crispy dosa with spicy potato filling", "80", "7 mins", R.drawable.sample_food));
        foodList.add(new FoodMenu("Pav Bhaji", "Spicy mashed veggies with buttered bread", "90", "5 mins", R.drawable.sample_food));
    }
}