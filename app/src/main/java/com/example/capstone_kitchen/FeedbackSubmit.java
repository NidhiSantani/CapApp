package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    }
}