package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;

public class CreatePasswordActivity extends AppCompatActivity {

    // Toolbar / Navigation
    private ImageView backButton;

    // New Password
    private EditText etNewPassword;
    private ImageView ivToggleNewPassword, ivCheckNewPassword;

    // Password Requirements Panel
    private LinearLayout layoutPasswordRequirements;
    private TextView tvReqUpper, tvReqLower, tvReqDigit, tvReqSpecial, tvReqLength;

    // Confirm Password
    private EditText etConfirmPassword;
    private ImageView ivToggleConfirmPassword, ivCheckConfirmPassword;

    // Submit
    private Button btnSubmit;

    // Track password visibility
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    // Track validity
    private boolean isNewPasswordValid = false;
    private boolean isConfirmPasswordValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // 2. Initialize Views
        backButton = findViewById(R.id.backButton);
        etNewPassword  = findViewById(R.id.etNewPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        ivCheckNewPassword  = findViewById(R.id.ivCheckNewPassword);

        layoutPasswordRequirements = findViewById(R.id.layoutPasswordRequirements);
        tvReqUpper = findViewById(R.id.tvReqUpper);
        tvReqLower = findViewById(R.id.tvReqLower);
        tvReqDigit = findViewById(R.id.tvReqDigit);
        tvReqSpecial = findViewById(R.id.tvReqSpecial);
        tvReqLength = findViewById(R.id.tvReqLength);

        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        ivCheckConfirmPassword = findViewById(R.id.ivCheckConfirmPassword);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setEnabled(false); // Initially disabled

        // 3. Back Arrow Click
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatePasswordActivity.this, OtpVerificationActivity.class);
            startActivity(intent);
        });

        // 4. Toggle Visibility: New Password
        ivToggleNewPassword.setOnClickListener(v -> {
            if (!isNewPasswordVisible) {
                // Show password
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleNewPassword.setImageResource(R.drawable.open_eye); // <-- Replace with your "eye open" drawable
                isNewPasswordVisible = true;
            } else {
                // Hide password
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleNewPassword.setImageResource(R.drawable.close_eye); // <-- Replace with your "eye closed" drawable
                isNewPasswordVisible = false;
            }
            // Keep cursor at the end
            etNewPassword.setSelection(etNewPassword.length());
        });

        // 5. Toggle Visibility: Confirm Password
        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (!isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.open_eye);
                isConfirmPasswordVisible = true;
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.close_eye);
                isConfirmPasswordVisible = false;
            }
            etConfirmPassword.setSelection(etConfirmPassword.length());
        });

        // 6. Watchers: New Password
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateNewPassword(s.toString());
                validateConfirmPassword(etConfirmPassword.getText().toString());
                updateSubmitButtonState();
            }
            @Override public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence c, int start, int before, int count) {}
        });

        // 7. Watchers: Confirm Password
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword(s.toString());
                updateSubmitButtonState();
            }
            @Override public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence c, int start, int before, int count) {}
        });

        // 8. Submit Button Click
        btnSubmit.setOnClickListener(v -> {
            if (isNewPasswordValid && isConfirmPasswordValid) {
                // Get the SAPID (Assume it's passed through Intent or available globally)
                String sapid = getIntent().getStringExtra("sapid");
                String password = etNewPassword.getText().toString();

                // Save the password to Firebase Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user")
                        .document(sapid)
                        .update("password", password)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreatePasswordActivity.this, "Password Created Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreatePasswordActivity.this, LoginPage.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CreatePasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    /**
     * Validates the new password for:
     *  - Uppercase
     *  - Lowercase
     *  - Digit
     *  - Special char
     *  - Length >= 8
     * Also shows/hides the password requirements layout accordingly.
     */
    private void validateNewPassword(String newPass) {
        boolean hasUpper   = newPass.matches(".*[A-Z].*");
        boolean hasLower   = newPass.matches(".*[a-z].*");
        boolean hasDigit   = newPass.matches(".*\\d.*");
        boolean hasSpecial = newPass.matches(".*[^a-zA-Z0-9].*");
        boolean hasLength  = newPass.length() >= 8;

        // Update UI colors for each requirement
        updateRequirementColor(tvReqUpper,   hasUpper);
        updateRequirementColor(tvReqLower,   hasLower);
        updateRequirementColor(tvReqDigit,   hasDigit);
        updateRequirementColor(tvReqSpecial, hasSpecial);
        updateRequirementColor(tvReqLength,  hasLength);

        // If all are true, password is valid
        isNewPasswordValid = (hasUpper && hasLower && hasDigit && hasSpecial && hasLength);

        // Show/hide the green check
        ivCheckNewPassword.setVisibility(isNewPasswordValid ? View.VISIBLE : View.GONE);

        // Animate the floating requirements panel:
        if (newPass.length() > 0 && !isNewPasswordValid) {
            // If not visible, fade in
            if (layoutPasswordRequirements.getVisibility() != View.VISIBLE) {
                layoutPasswordRequirements.setAlpha(0f);
                layoutPasswordRequirements.setVisibility(View.VISIBLE);
                layoutPasswordRequirements.animate().alpha(1f).setDuration(300).start();
            }
        } else {
            // If visible and conditions met (or field is empty), fade out
            if (layoutPasswordRequirements.getVisibility() == View.VISIBLE) {
                layoutPasswordRequirements.animate().alpha(0f).setDuration(300).withEndAction(() ->
                        layoutPasswordRequirements.setVisibility(View.GONE)
                ).start();
            }
        }
    }

    /**
     * Confirm password must match new password and new password must be valid.
     */
    private void validateConfirmPassword(String confirmPass) {
        String newPass = etNewPassword.getText().toString();
        if (isNewPasswordValid && confirmPass.equals(newPass) && confirmPass.length() >= 8) {
            isConfirmPasswordValid = true;
            ivCheckConfirmPassword.setVisibility(View.VISIBLE);
        } else {
            isConfirmPasswordValid = false;
            ivCheckConfirmPassword.setVisibility(View.GONE);
        }
    }

    /**
     * Change the color of the requirement text based on whether it's met (green) or not (red).
     */
    private void updateRequirementColor(TextView textView, boolean isMet) {
        if (isMet) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    /**
     * Enables the submit button if both fields are valid.
     */
    private void updateSubmitButtonState() {
        if (isNewPasswordValid && isConfirmPasswordValid) {
            btnSubmit.setEnabled(true);
            btnSubmit.setAlpha(1f);
        } else {
            btnSubmit.setEnabled(false);
            btnSubmit.setAlpha(0.5f);
        }
    }
}