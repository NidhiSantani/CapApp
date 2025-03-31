package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewBestsellers;
    private CategoryAdapter categoryAdapter;
    private BestsellerAdapter bestsellerAdapter;
    private List<Category> categoryList;
    private List<Bestseller> bestsellerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Adjust insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---------------- Setup Categories RecyclerView ----------------
        recyclerViewCategories = findViewById(R.id.categories_recycler);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        categoryList = new ArrayList<>();
        categoryList.add(new Category("Vadapav", R.drawable.vadapav));
        categoryList.add(new Category("Dabeli", R.drawable.vadapav));
        categoryList.add(new Category("Pavbhaji", R.drawable.vadapav));
        categoryList.add(new Category("Chinese Bhel", R.drawable.vadapav));
        categoryList.add(new Category("Maggie", R.drawable.vadapav));
        categoryList.add(new Category("Pasta", R.drawable.vadapav));

        // Handle click to open MenuPage
        categoryAdapter = new CategoryAdapter(this, categoryList, category -> {
            Intent intent = new Intent(HomePage.this, MenuPage.class);
            intent.putExtra("category_name", category.getName());
            intent.putExtra("category_image", category.getImageResId());
            startActivity(intent);
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

        // ---------------- Setup Bestsellers RecyclerView ----------------
        recyclerViewBestsellers = findViewById(R.id.bestsellers_recycler);
        recyclerViewBestsellers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        bestsellerList = new ArrayList<>();
        bestsellerList.add(new Bestseller("Omelette", R.drawable.vadapav));
        bestsellerList.add(new Bestseller("Sandwich", R.drawable.vadapav));
        bestsellerList.add(new Bestseller("Burger", R.drawable.vadapav));

        bestsellerAdapter = new BestsellerAdapter(this, bestsellerList);
        recyclerViewBestsellers.setAdapter(bestsellerAdapter);

        ImageButton menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this,SideNavigation.class));
            }
        });

        // ---------------- Setup Bottom Navigation ----------------
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(HomePage.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(HomePage.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(HomePage.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(HomePage.this, Cart.class));
                    return true;
                }

                return false;
            }
        });
    }
}