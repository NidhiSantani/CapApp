package com.example.capstone_kitchen;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.media.MediaPlayer;
import android.os.Handler;

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

        // Reference the tick ImageView
        imgPaySuccess = findViewById(R.id.imgPaySuccess);

        // Start circular reveal animation immediately
        imgPaySuccess.post(() -> startCircularRevealAnimation());

        // Play success sound
        playSuccessSound();

        // Automatically move to the next activity after 6 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PaymentSuccess.this, Invoice.class);
            startActivity(intent);
            finish();
        }, AUTO_NAVIGATION_DELAY);

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(PaymentSuccess.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(PaymentSuccess.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(PaymentSuccess.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(PaymentSuccess.this, Payment.class));
                    return true;
                }

                return false;
            }
        });
    }

    private void startCircularRevealAnimation() {
        // Get center of the image for circular effect
        int cx = imgPaySuccess.getWidth() / 2;
        int cy = imgPaySuccess.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        // Create circular reveal animation
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