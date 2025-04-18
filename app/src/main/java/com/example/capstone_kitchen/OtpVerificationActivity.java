package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

public class OtpVerificationActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvResendInfo;
    private Button btnVerify;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private CountDownTimer countDownTimer;
    private static final long OTP_TIMEOUT = 30000; // 30 seconds

    private String verificationId, phone, sapid;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        firebaseAuth = FirebaseAuth.getInstance();

        // Get data from SignupPage
        verificationId = getIntent().getStringExtra("verificationId");
        phone = getIntent().getStringExtra("phone");
        sapid = getIntent().getStringExtra("sapid");

        ivBack = findViewById(R.id.backButton);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(OtpVerificationActivity.this, SignupPage.class);
            startActivity(intent);
            finish();
        });

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);

        // Set up OTP fields
        setupOtpField(etOtp1, etOtp2);
        setupOtpField(etOtp2, etOtp3);
        setupOtpField(etOtp3, etOtp4);
        setupOtpField(etOtp4, etOtp5);
        setupOtpField(etOtp5, etOtp6);

        tvResendInfo = findViewById(R.id.tvResendInfo);
        startOtpCountdown();

        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(v -> {
            String otp = etOtp1.getText().toString().trim()
                    + etOtp2.getText().toString().trim()
                    + etOtp3.getText().toString().trim()
                    + etOtp4.getText().toString().trim()
                    + etOtp5.getText().toString().trim()
                    + etOtp6.getText().toString().trim();

            if (otp.length() == 6 && verificationId != null) {
                verifyOtp(otp);
            } else {
                Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
            }
        });

        tvResendInfo.setOnClickListener(v -> {
            if (!tvResendInfo.isEnabled()) return;
            resendOtp(); // logic to resend
        });
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, CreatePasswordActivity.class);
                        intent.putExtra("sapid", sapid);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupOtpField(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) next.requestFocus();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

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

    private void resendOtp() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)       // phone must include +91 prefix
                .setTimeout(30L, java.util.concurrent.TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Optional: auto verification
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpVerificationActivity.this, "Resend failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Toast.makeText(OtpVerificationActivity.this, "OTP resent successfully", Toast.LENGTH_SHORT).show();
                        verificationId = newVerificationId;

                        // Clear old inputs
                        etOtp1.setText(""); etOtp2.setText("");
                        etOtp3.setText(""); etOtp4.setText("");
                        etOtp5.setText(""); etOtp6.setText("");
                        etOtp1.requestFocus();

                        // Restart timer
                        startOtpCountdown();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}