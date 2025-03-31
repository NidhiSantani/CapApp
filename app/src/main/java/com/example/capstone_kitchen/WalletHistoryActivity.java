package com.example.capstone_kitchen;

import static com.example.capstone_kitchen.R.id.tvToolbarTitle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_kitchen.WalletHistoryAdapter;
import com.example.capstone_kitchen.WalletTransactionModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a list of wallet transactions.
 */
public class WalletHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewWalletHistory;
    private WalletHistoryAdapter adapter;
    private List<WalletTransactionModel> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);

        // 1. Set up the custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hide default title if using a custom TextView
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 2. Toolbar buttons
        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        // Handle back button click
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletHistoryActivity.this, SideNavigation.class));
            }
        });

        // (Optional) Handle menu button click
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletHistoryActivity.this, SideNavigation.class));
            }
        });

        // 3. Initialize RecyclerView
        recyclerViewWalletHistory = findViewById(R.id.recyclerViewWalletHistory);
        recyclerViewWalletHistory.setLayoutManager(new LinearLayoutManager(this));

        // 4. Prepare sample data (In a real app, fetch from a server or database)
        transactionList = new ArrayList<>();
        transactionList.add(new WalletTransactionModel(
                "Order Placed",
                "February 28, 2025 | 13:26 PM",
                72.0,
                false // isCredit=false => debit
        ));
        transactionList.add(new WalletTransactionModel(
                "Wallet top-up via admin",
                "February 21, 2025 | 04:00 AM",
                300.0,
                true // isCredit=true => credit
        ));
        transactionList.add(new WalletTransactionModel(
                "Order Placed",
                "February 14, 2025 | 13:01 PM",
                10.0,
                false
        ));
        transactionList.add(new WalletTransactionModel(
                "Order Placed",
                "February 10, 2025 | 09:34 AM",
                250.0,
                false // If it's an order, presumably negative. You can store negative amounts if you prefer.
        ));

        // 5. Set up adapter
        adapter = new WalletHistoryAdapter(transactionList);
        recyclerViewWalletHistory.setAdapter(adapter);


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(WalletHistoryActivity.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(WalletHistoryActivity.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(WalletHistoryActivity.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(WalletHistoryActivity.this, Cart.class));
                    return true;
                }

                return false;
            }
        });

    }
}