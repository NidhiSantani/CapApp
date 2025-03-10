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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView ivBack, ivMenu, ivToggleNewPassword, ivToggleConfirmPassword;
    private ImageView ivCheckNewPassword, ivCheckConfirmPassword;
    private TextView tvToolbarTitle;
    private EditText etNewPassword, etConfirmPassword;
    // Password requirement TextViews
    private TextView tvReqUpper, tvReqLower, tvReqDigit, tvReqSpecial, tvReqLength;
    private LinearLayout layoutPasswordRequirements;
    private Button btnSubmit;

    private boolean isNewPasswordVisible = false;       // Tracks toggle state
    private boolean isConfirmPasswordVisible = false;   // Tracks toggle state

    private boolean isNewPasswordValid = false;         // Tracks validation state
    private boolean isConfirmPasswordValid = false;     // Tracks validation state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // 1. Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 2. Find the layout
        layoutPasswordRequirements = findViewById(R.id.layoutPasswordRequirements);

        // 3. Initialize toolbar views
        ivBack = findViewById(R.id.ivBack);
        ivMenu = findViewById(R.id.ivMenu);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        // 4. Password fields & icons
        etNewPassword          = findViewById(R.id.etNewPassword);
        ivToggleNewPassword    = findViewById(R.id.ivToggleNewPassword);
        ivCheckNewPassword     = findViewById(R.id.ivCheckNewPassword);

        etConfirmPassword      = findViewById(R.id.etConfirmPassword);
        ivToggleConfirmPassword= findViewById(R.id.ivToggleConfirmPassword);
        ivCheckConfirmPassword = findViewById(R.id.ivCheckConfirmPassword);

        // 5. Password requirement TextViews
        tvReqUpper   = findViewById(R.id.tvReqUpper);
        tvReqLower   = findViewById(R.id.tvReqLower);
        tvReqDigit   = findViewById(R.id.tvReqDigit);
        tvReqSpecial = findViewById(R.id.tvReqSpecial);
        tvReqLength  = findViewById(R.id.tvReqLength);

        // 6. Submit button
        btnSubmit = findViewById(R.id.btnSubmit);

        // 7. Handle toolbar icons
        ivBack.setOnClickListener(v -> finish());
        ivMenu.setOnClickListener(v -> {
            // Open a menu or navigation drawer if needed
        });

        // 8. Toggle visibility for New Password
        ivToggleNewPassword.setOnClickListener(v -> {
            if (!isNewPasswordVisible) {
                // Show password
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleNewPassword.setImageResource(R.drawable.open_eye);
                isNewPasswordVisible = true;
            } else {
                // Hide password
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleNewPassword.setImageResource(R.drawable.close_eye);
                isNewPasswordVisible = false;
            }
            // Move cursor to the end
            etNewPassword.setSelection(etNewPassword.length());
        });

        // 9. Toggle visibility for Confirm Password
        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (!isConfirmPasswordVisible) {
                // Show password
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.open_eye);
                isConfirmPasswordVisible = true;
            } else {
                // Hide password
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.close_eye);
                isConfirmPasswordVisible = false;
            }
            // Move cursor to the end
            etConfirmPassword.setSelection(etConfirmPassword.length());
        });

        // 10. Add text watchers for validation
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateNewPassword(s.toString());
                validateConfirmPassword(etConfirmPassword.getText().toString()); // re-check confirm field
                updateSubmitButtonState();
            }
            @Override public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence c, int i, int i1, int i2) {}
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword(s.toString());
                updateSubmitButtonState();
            }
            @Override public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence c, int i, int i1, int i2) {}
        });

        // 11. Handle Submit button click
        btnSubmit.setOnClickListener(v -> {
            if (isNewPasswordValid && isConfirmPasswordValid) {
                // Perform password reset logic here
                // e.g., update on server, show success message, etc.
                Toast.makeText(ResetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, OrderHistoryActivity.class));
                finish();
            }
        });
    }

    /**
     * Validate the new password against multiple requirements:
     * 1) Uppercase letter
     * 2) Lowercase letter
     * 3) Digit
     * 4) Special character
     * 5) Minimum length (8 in this example)
     */
    private void validateNewPassword(String newPass) {
        boolean hasUpper   = newPass.matches(".*[A-Z].*");
        boolean hasLower   = newPass.matches(".*[a-z].*");
        boolean hasDigit   = newPass.matches(".*\\d.*");
        boolean hasSpecial = newPass.matches(".*[^a-zA-Z0-9].*"); // non-alphanumeric
        boolean hasLength  = newPass.length() >= 8;

        // Update each requirement TextView color
        updateRequirementColor(tvReqUpper,   hasUpper);
        updateRequirementColor(tvReqLower,   hasLower);
        updateRequirementColor(tvReqDigit,   hasDigit);
        updateRequirementColor(tvReqSpecial, hasSpecial);
        updateRequirementColor(tvReqLength,  hasLength);

        // If all are true, password is valid
        isNewPasswordValid = (hasUpper && hasLower && hasDigit && hasSpecial && hasLength);

        // Show/hide green check icon
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
     * Confirm password must match new password and also meet the same length requirement
     * (but typically if new password is valid, we just check if they're equal).
     */
    private void validateConfirmPassword(String confirmPass) {
        String newPass = etNewPassword.getText().toString();
        // If new password is valid and confirm matches
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
