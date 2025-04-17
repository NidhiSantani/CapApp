package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivMenu;
    private TextView tvToolbarTitle, tvUserName, tvUserID, tvUserPhone;
    private TextView tvResetPassword, tvLogout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the SAP ID passed from SideNavigation
        String sapid = getIntent().getStringExtra("sapid");

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

        // 3. User info TextViews
        tvUserName = findViewById(R.id.tvUserName);
        tvUserID = findViewById(R.id.tvUserID);
        tvUserPhone = findViewById(R.id.tvUserPhone);

        // 4. Footer links
        tvResetPassword = findViewById(R.id.tvResetPassword);
        tvLogout = findViewById(R.id.tvLogout);

        // 5. Set up click listeners
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomePage.class));
            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SideNavigation.class));
            }
        });

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, Logout.class));
            }
        });

        // 6. Fetch user data from Firestore using the sapid
        fetchUserData(sapid);

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

    // Method to fetch user data from Firestore using SAP ID
    private void fetchUserData(String sapid) {
        // Reference to the user document using SAP ID as document ID
        DocumentReference userRef = db.collection("user").document(sapid);

        // Fetch the document
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve user data from the document
                    String username = document.getString("username");
                    String mobile = document.getString("mobile");

                    // Set data to the TextViews
                    tvUserName.setText(username);
                    tvUserID.setText(sapid);  // SAP ID (Document ID)
                    tvUserPhone.setText(mobile);
                } else {
                    Toast.makeText(ProfileActivity.this, "No such user found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}