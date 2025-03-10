package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        ivBack.setOnClickListener(view -> finish());  // Go back to previous activity

        ivMenu.setOnClickListener(view -> {
            // Open menu or navigation drawer, if needed
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
                // Handle "Logout" click
                // E.g., clear session and go back to login screen
            }
        });

        // 6. (Optional) Load actual user data
        // For demonstration, we set static data
        tvUserName.setText("Richard Jones");
        tvUserID.setText("70412110223");
        tvUserPhone.setText("+91 1234567890");
    }
}