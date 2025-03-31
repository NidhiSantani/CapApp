package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Feedback extends AppCompatActivity {

    private EditText feedbackInput;
    private TextView wordCount;
    private Button submitButton;
    private Spinner categoryDropdown, foodDropdown;
    private ImageView[] stars = new ImageView[5];
    private int rating = 0;
    private static final int MAX_WORD_LIMIT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.feedback);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottomnav_home) {
                startActivity(new Intent(Feedback.this, HomePage.class));
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                startActivity(new Intent(Feedback.this, Favorites.class));
                return true;
            } else if (itemId == R.id.bottomnav_wallet) {
                startActivity(new Intent(Feedback.this, VirtualWallet.class));
                return true;
            } else if (itemId == R.id.bottomnav_cart) {
                startActivity(new Intent(Feedback.this, Cart.class));
                return true;
            }
            return false;
        });

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(view -> startActivity(new Intent(Feedback.this, SideNavigation.class)));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> startActivity(new Intent(Feedback.this, HomePage.class)));

        feedbackInput = findViewById(R.id.feedbackInput);
        wordCount = findViewById(R.id.wordCount);
        submitButton = findViewById(R.id.submitPinButton);

        // Disable submit button initially
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.5f);

        // Feedback word count logic
        feedbackInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateWordCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton.setOnClickListener(v -> {
            Toast.makeText(this, "Feedback submitted!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Feedback.this, FeedbackSubmit.class));
        });

        // Dropdown Setup
        categoryDropdown = findViewById(R.id.categoryDropdown);
        foodDropdown = findViewById(R.id.foodDropdown);

        setupDropdowns();

        // Star Rating Setup
        setupStarRating();
    }

    private void updateWordCount(String text) {
        String[] words = text.trim().split("\\s+");
        int wordCountValue = text.trim().isEmpty() ? 0 : words.length;

        wordCount.setText(wordCountValue + "/100 words");

        if (wordCountValue > MAX_WORD_LIMIT) {
            Toast.makeText(this, "Word limit of 100 reached!", Toast.LENGTH_SHORT).show();
            feedbackInput.setText(text.substring(0, text.lastIndexOf(" ")));
            feedbackInput.setSelection(feedbackInput.getText().length());
        }

        // Enable/disable submit button based on input
        boolean hasText = wordCountValue > 0;
        submitButton.setEnabled(hasText);
        submitButton.setAlpha(hasText ? 1.0f : 0.5f);
    }

    private void setupDropdowns() {
        String[] categories = {"Select Category", "Snacks", "Beverages", "Meals"};
        String[] foodItems = {"Select Food Item", "Burger", "Pizza", "Pasta", "Coffee"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, foodItems);

        categoryDropdown.setAdapter(categoryAdapter);
        foodDropdown.setAdapter(foodAdapter);
    }

    private void setupStarRating() {
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        for (int i = 0; i < stars.length; i++) {
            int index = i;
            stars[i].setOnClickListener(v -> setRating(index + 1));
        }
    }

    private void setRating(int newRating) {
        rating = newRating;

        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.star_filled);
            } else {
                stars[i].setImageResource(R.drawable.star_unfilled);
            }
        }
    }
}