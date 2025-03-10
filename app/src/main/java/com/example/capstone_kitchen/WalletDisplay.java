package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WalletDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.wallet_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backbtn = findViewById(R.id.backButton);
        backbtn.setOnClickListener(view -> {
            Intent intent = new Intent(WalletDisplay.this, WalletPin.class);
            startActivity(intent);
        });

        TextView resetPin = findViewById(R.id.resetPin);
        resetPin.setOnClickListener(view -> {
            Intent intent = new Intent(WalletDisplay.this, WalletPin.class);
            startActivity(intent);
        });
    }
}