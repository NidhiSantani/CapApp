package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class OtpVerificationActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvResendInfo;
    private Button btnVerify;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private CountDownTimer countDownTimer;
    private static final long OTP_TIMEOUT = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // 2. Toolbar views
        ivBack = findViewById(R.id.backButton);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtpVerificationActivity.this, SignupPage.class);
                startActivity(intent);
            }
        });

        // 3. OTP fields
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);

        // Add watchers for each OTP field to move focus automatically
        setupOtpField(etOtp1, etOtp2);
        setupOtpField(etOtp2, etOtp3);
        setupOtpField(etOtp3, etOtp4);
        // For the last field, you might verify automatically or wait for the user to press the button

        // 4. Resend info (countdown)
        tvResendInfo = findViewById(R.id.tvResendInfo);
        startOtpCountdown();

        // 5. Verify button
        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(v -> {
            // Gather OTP
            String otp = etOtp1.getText().toString().trim()
                    + etOtp2.getText().toString().trim()
                    + etOtp3.getText().toString().trim()
                    + etOtp4.getText().toString().trim();

            if (otp.length() == 4) {
                // TODO: Verify OTP with your backend or logic
                Toast.makeText(OtpVerificationActivity.this, "OTP Verified: " + otp, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpVerificationActivity.this, CreatePasswordActivity.class); // Change to your next screen
                startActivity(intent);
                finish(); // Finish OTP screen after success
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show();
            }
        });

        // Optionally, if you want to handle a "resend" click after the timer ends:
        tvResendInfo.setOnClickListener(v -> {
            if (tvResendInfo.getText().toString().startsWith("Didn’t receive")) {
                // Timer hasn't ended yet, do nothing
            } else {
                // Resend OTP logic
                resendOtp();
            }
        });
    }

    /**
     * Moves focus to the next EditText when a single digit is entered.
     */
    private void setupOtpField(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    next.requestFocus();
                }
            }
            @Override public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence c, int i, int i1, int i2) {}
        });
    }

    /**
     * Starts a 30-second countdown for "Retry in X seconds".
     */
    private void startOtpCountdown() {
        tvResendInfo.setText("Didn’t receive the code? Retry in 30 seconds");
        tvResendInfo.setEnabled(false);

        countDownTimer = new CountDownTimer(OTP_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvResendInfo.setText("Didn’t receive the code? Retry in " + seconds + " seconds");
            }

            @Override
            public void onFinish() {
                tvResendInfo.setText("Didn’t receive the code? Tap to resend.");
                tvResendInfo.setEnabled(true);
            }
        }.start();
    }

    /**
     * Simulates the resend OTP logic, e.g., request a new code from the server.
     * Resets the countdown.
     */
    private void resendOtp() {
        Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show();
        // Clear OTP fields
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");
        etOtp1.requestFocus();

        // Restart the countdown
        startOtpCountdown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}