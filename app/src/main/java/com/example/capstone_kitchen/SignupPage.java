package com.example.capstone_kitchen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupPage extends AppCompatActivity {

    Button signupButton;
    EditText username, phone, sapid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        sapid = findViewById(R.id.sapid);
        signupButton = findViewById(R.id.signupButton);

        // Back button logic
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> startActivity(new Intent(SignupPage.this, WelcomePage.class)));

        // Real-time validation
        username.addTextChangedListener(new ValidationWatcher(username));
        phone.addTextChangedListener(new ValidationWatcher(phone));
        sapid.addTextChangedListener(new ValidationWatcher(sapid));

        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                startActivity(new Intent(SignupPage.this, OtpVerificationActivity.class));
            }
        });
    }

    // Real-time validation logic
    private class ValidationWatcher implements TextWatcher {
        private final EditText editText;

        ValidationWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateInputs();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    // Final validation on Sign Up button click
    private boolean validateInputs() {
        boolean isValid = true;

        String name = username.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        String sapId = sapid.getText().toString().trim();

        if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
            username.setError("Name must only contain letters");
            isValid = false;
        }

        if (phoneNumber.isEmpty()) {
            phone.setError("Please enter your phone number");
            isValid = false;
        } else if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d+")) {
            phone.setError("Phone number must be 10 digits");
            isValid = false;
        }

        if (sapId.isEmpty()) {
            sapid.setError("Please enter your SAP ID");
            isValid = false;
        } else if (sapId.length() != 11 || !sapId.matches("\\d+")) {
            sapid.setError("SAP ID must be 11 digits");
            isValid = false;
        }

        return isValid;
    }
}
