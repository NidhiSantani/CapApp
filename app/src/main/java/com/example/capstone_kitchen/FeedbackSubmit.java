package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedbackSubmit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.feedback_submit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String sapid = getIntent().getStringExtra("sapid");

        Button goHomeButton = findViewById(R.id.goHomeButton);
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackSubmit.this, HomePage.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            }
        });

        ImageButton backbtn = findViewById(R.id.backButton);
        backbtn.setOnClickListener(view -> {
            Intent intent = new Intent(FeedbackSubmit.this, Feedback.class);
            startActivity(intent);
            finish();
        });

        ImageButton menubtn = findViewById(R.id.menuButton);
        menubtn.setOnClickListener(view -> {
            Intent intent = new Intent(FeedbackSubmit.this, SideNavigation.class);
            startActivity(intent);
        });


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    Intent intent = new Intent(FeedbackSubmit.this, HomePage.class);
                    intent.putExtra("sapid", sapid);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(FeedbackSubmit.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(FeedbackSubmit.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(FeedbackSubmit.this, Cart.class));
                    return true;
                }

                return false;
            }
        });

    }
}