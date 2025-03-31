package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Payment extends AppCompatActivity {

    private RadioButton rbUPI, rbWallet;
    Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnPlaceOrder = findViewById((R.id.btnPlaceOrder));
        rbUPI = findViewById(R.id.rbUPI);
        rbWallet = findViewById(R.id.rbWallet);

        // Custom RadioGroup behavior
        View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == rbUPI) {
                    rbUPI.setChecked(true);
                    rbWallet.setChecked(false);
                } else if (v == rbWallet) {
                    rbWallet.setChecked(true);
                    rbUPI.setChecked(false);

                    // Intent to navigate to the next page when Wallet is selected
                    Intent intent = new Intent(Payment.this, PaymentWallet.class);
                    intent.putExtra("walletSelected", true);
                    startActivity(intent);
                }
            }
        };

        rbUPI.setOnClickListener(radioButtonClickListener);
        rbWallet.setOnClickListener(radioButtonClickListener);


        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbUPI.isChecked()) {
                    // Navigate to Payment Success Page
                    Intent intent = new Intent(Payment.this, PaymentSuccess.class);
                    startActivity(intent);
                    finish(); // Optional: Close current activity
                } else {
                    Toast.makeText(Payment.this, "Please select UPI to proceed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Payment.this, Cart.class));
            }
        });


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(Payment.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(Payment.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(Payment.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(Payment.this, Cart.class));
                    return true;
                }

                return false;
            }
        });
    }
}