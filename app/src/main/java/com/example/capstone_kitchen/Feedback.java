package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Typeface;

public class Feedback extends AppCompatActivity {

    private EditText feedbackInput;
    private TextView wordCount;
    private Button submitButton;
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

        ImageButton menubtn = findViewById(R.id.menuButton);
        menubtn.setOnClickListener(view -> {
            Intent intent = new Intent(Feedback.this, SideNavigation.class);
            startActivity(intent);
        });


        feedbackInput = findViewById(R.id.feedbackInput);
        wordCount = findViewById(R.id.wordCount);
        submitButton = findViewById(R.id.submitPinButton);

        //Initially disable submit button
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.5f); // Reduce opacity to show it's disabled

        feedbackInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateWordCount(s.toString());
                if (s.length() > 0) {
                    feedbackInput.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                } else {
                    feedbackInput.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton.setOnClickListener(v -> {
            wordCount.setText("0/100 words"); // Reset word counter
            Intent intent = new Intent(Feedback.this, FeedbackSubmit.class);
            startActivity(intent);
            finish();
        });

    }

    //Functions
    private void updateWordCount(String text) {
        String[] words = text.trim().split("\\s+");
        int wordCountValue = text.trim().isEmpty() ? 0 : words.length;

        wordCount.setText(wordCountValue + "/100 words");

        if (wordCountValue > MAX_WORD_LIMIT) {
            Toast.makeText(this, "Word limit of 200 reached!", Toast.LENGTH_SHORT).show();
            feedbackInput.setText(text.substring(0, text.lastIndexOf(" "))); // Remove last word
            feedbackInput.setSelection(feedbackInput.getText().length()); // Move cursor to end
        }

        // Enable submit button only if there is text
        boolean hasText = wordCountValue > 0;
        submitButton.setEnabled(hasText);
        submitButton.setAlpha(hasText ? 1.0f : 0.5f);
    }
}