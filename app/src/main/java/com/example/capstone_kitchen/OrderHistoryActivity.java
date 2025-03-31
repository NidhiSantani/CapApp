package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderHistory;
    private ImageView ivBack, ivMenu;
    private OrderHistoryAdapter adapter;
    private List<OrderHistoryModel> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the default title if you're using a custom TextView
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ivBack = findViewById(R.id.ivBack);
        ivMenu = findViewById(R.id.ivMenu);

        // Initialize RecyclerView
        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        List<OrderHistoryModel> orders = new ArrayList<>();

        // Single order with multiple items
        List<OrderItemModel> itemsDay1 = new ArrayList<>();
        itemsDay1.add(new OrderItemModel("Vadapav", 1, 50.00));
        itemsDay1.add(new OrderItemModel("Maggi", 2, 70.00));
        orders.add(new OrderHistoryModel("February 21, 2025", "04:00 AM", "Completed", itemsDay1));

        // Another order
        List<OrderItemModel> itemsDay2 = new ArrayList<>();
        itemsDay2.add(new OrderItemModel("Vadapav", 1, 50.00));
        orders.add(new OrderHistoryModel("February 28, 2025", "13:26 PM", "Cancelled", itemsDay2));

        // Set up RecyclerView
        OrderHistoryAdapter adapter = new OrderHistoryAdapter(orders);
        recyclerViewOrderHistory.setAdapter(adapter);

        ivBack.setOnClickListener(view -> {
            startActivity(new Intent(OrderHistoryActivity.this, HomePage.class));
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderHistoryActivity.this, SideNavigation.class));
            }
        });


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(OrderHistoryActivity.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(OrderHistoryActivity.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(OrderHistoryActivity.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(OrderHistoryActivity.this, Cart.class));
                    return true;
                }

                return false;
            }
        });


    }
}