package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        RelativeLayout profile = findViewById(R.id.rl_profile);
        RelativeLayout orderhist = findViewById(R.id.rl_orderhist);
        RelativeLayout wallethist = findViewById(R.id.rl_wallethist);
        RelativeLayout feedback = findViewById(R.id.rl_feedback);

        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(SideNavigation.this, Feedback.class);
            startActivity(intent);
        });


    }
}