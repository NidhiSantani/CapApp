package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivMenu;
    private TextView tvToolbarTitle, tvUserName, tvUserID, tvUserPhone;
    private TextView tvResetPassword, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hide default title if using a custom TextView
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 2. Initialize toolbar views
        ivBack = findViewById(R.id.ivBack);
        ivMenu = findViewById(R.id.ivMenu);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        // 3. User info
        tvUserName = findViewById(R.id.tvUserName);
        tvUserID   = findViewById(R.id.tvUserID);
        tvUserPhone= findViewById(R.id.tvUserPhone);

        // 4. Footer links
        tvResetPassword = findViewById(R.id.tvResetPassword);
        tvLogout        = findViewById(R.id.tvLogout);

        // 5. Set up click listeners
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomePage.class));
            }
        });  // Go back to previous activity

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SideNavigation.class));
            }
        });

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "Reset Password?" click
                startActivity(new Intent(ProfileActivity.this, ResetPasswordActivity.class));
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, Logout.class));
            }
        });

        // 6. (Optional) Load actual user data
        // For demonstration, we set static data
        tvUserName.setText("Richard Jones");
        tvUserID.setText("70412110223");
        tvUserPhone.setText("+91 1234567890");


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(ProfileActivity.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(ProfileActivity.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(ProfileActivity.this, Cart.class));
                    return true;
                }

                return false;
            }
        });

    }
}