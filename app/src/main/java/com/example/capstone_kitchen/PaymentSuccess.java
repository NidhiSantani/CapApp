package com.example.capstone_kitchen;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PaymentSuccess extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView imgPaySuccess;
    private static final int ANIMATION_DURATION = 2000; // 2 seconds
    private static final int AUTO_NAVIGATION_DELAY = 6000; // 6 seconds

    private String sapid, orderId;
    private double totalAmount;
    private TextView tvPaymentamt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Fetch values from Intent
        Intent intent = getIntent();
        sapid = intent.getStringExtra("sapid");
        orderId = intent.getStringExtra("orderId");
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0);

        // Reference the TextView
        tvPaymentamt = findViewById(R.id.tvPaymentamt);

        // Set the totalAmount to the TextView
        tvPaymentamt.setText("₹ " + String.format("%.2f", totalAmount));

        // Reference the tick ImageView
        imgPaySuccess = findViewById(R.id.imgPaySuccess);

        // Start circular reveal animation immediately
        imgPaySuccess.post(this::startCircularRevealAnimation);

        // Play success sound
        playSuccessSound();

        // Automatically move to the next activity after 6 seconds
        new Handler().postDelayed(() -> {
            Intent nextIntent = new Intent(PaymentSuccess.this, Invoice.class);
            nextIntent.putExtra("sapid", sapid);
            nextIntent.putExtra("orderId", orderId);
            nextIntent.putExtra("totalAmount", totalAmount);
            startActivity(nextIntent);
            finish();
        }, AUTO_NAVIGATION_DELAY);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> startActivity(new Intent(PaymentSuccess.this, Cart.class)));

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottomnav_home) {
                startActivity(new Intent(PaymentSuccess.this, HomePage.class));
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                startActivity(new Intent(PaymentSuccess.this, Favorites.class));
                return true;
            } else if (itemId == R.id.bottomnav_wallet) {
                startActivity(new Intent(PaymentSuccess.this, VirtualWallet.class));
                return true;
            } else if (itemId == R.id.bottomnav_cart) {
                startActivity(new Intent(PaymentSuccess.this, Cart.class));
                return true;
            }

            return false;
        });
    }

    private void startCircularRevealAnimation() {
        int cx = imgPaySuccess.getWidth() / 2;
        int cy = imgPaySuccess.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(imgPaySuccess, cx, cy, 0, finalRadius);
        circularReveal.setDuration(ANIMATION_DURATION);
        imgPaySuccess.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private void playSuccessSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.paymentsucesstone);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}