package com.example.capstone_kitchen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SideNavigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.side_navigation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the SAP ID passed from HomePage
        String sapid = getIntent().getStringExtra("sapid");

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SideNavigation.this, SideNavigation.class));
            }
        });

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(SideNavigation.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(SideNavigation.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(SideNavigation.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(SideNavigation.this, Cart.class));
                    return true;
                }

                return false;
            }
        });

        RelativeLayout profile = findViewById(R.id.rl_profile);
        RelativeLayout orderhist = findViewById(R.id.rl_orderhist);
        RelativeLayout wallethist = findViewById(R.id.rl_wallethist);
        RelativeLayout feedback = findViewById(R.id.rl_feedback);

        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(SideNavigation.this, Feedback.class);
            intent.putExtra("sapid", sapid);
            startActivity(intent);
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the SAP ID from the current activity (SideNavigation)
                String sapid = getIntent().getStringExtra("sapid");

                // Create an Intent to start ProfileActivity
                Intent intent = new Intent(SideNavigation.this, ProfileActivity.class);

                // Put the SAP ID into the Intent to pass it to ProfileActivity
                intent.putExtra("sapid", sapid);

                // Start ProfileActivity
                startActivity(intent);
            }
        });

        orderhist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SideNavigation.this, OrderHistoryActivity.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            }
        });

        wallethist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SideNavigation.this, WalletHistoryActivity.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SideNavigation.this, HomePage.class));
            }
        });

    }
}