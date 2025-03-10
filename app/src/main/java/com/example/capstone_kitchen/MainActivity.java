package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import correct Handler class

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 4000; // 4 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Using Handler to delay transition to next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Navigate to the next activity
                Intent intent = new Intent(MainActivity.this, WelcomePage.class); // Ensure MainActivity2 exists
                startActivity(intent);
                finish();  // Closes this activity so the user can't go back to it
            }
        }, SPLASH_DELAY);
    }
}