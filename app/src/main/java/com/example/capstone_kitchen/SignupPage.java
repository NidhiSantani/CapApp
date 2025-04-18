package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignupPage extends AppCompatActivity {

    Button signupButton;
    EditText username, phone, sapid;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        sapid = findViewById(R.id.sapid);
        signupButton = findViewById(R.id.signupButton);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> startActivity(new Intent(SignupPage.this, WelcomePage.class)));

        username.addTextChangedListener(new ValidationWatcher(username));
        phone.addTextChangedListener(new ValidationWatcher(phone));
        sapid.addTextChangedListener(new ValidationWatcher(sapid));

        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                createUserInFirestoreAndSendOTP();
            }
        });
    }

    private class ValidationWatcher implements TextWatcher {
        private final EditText editText;

        ValidationWatcher(EditText editText) {
            this.editText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateInputs();
        }
        public void afterTextChanged(Editable s) {}
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String name = username.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        String sapId = sapid.getText().toString().trim();

        if (name.isEmpty() || !name.matches("[a-zA-Z ]+") || name.length() < 3) {
            username.setError("Name must be at least 3 characters and only contain letters");
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

    private void createUserInFirestoreAndSendOTP() {
        String name = username.getText().toString().trim();
        long mobileNumber = Long.parseLong(phone.getText().toString().trim());
        String sapId = sapid.getText().toString().trim();
        Timestamp now = Timestamp.now();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("created_at", now);
        userMap.put("last_updated", now);
        userMap.put("favorites", new ArrayList<String>());
        userMap.put("mobile", mobileNumber);
        userMap.put("password", "");
        userMap.put("upi_id", "");
        userMap.put("username", name);
        userMap.put("wallet_balance", 0);
        userMap.put("wallet_id", "");
        userMap.put("wallet_pin", 0);

        db.collection("user").document(sapId)
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    String mobileWithCountryCode = "+91" + phone.getText().toString().trim();
                    sendOTP(mobileWithCountryCode, sapId);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupPage.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void sendOTP(String phoneNumber, String sapId) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Auto verification logic (optional)
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SignupPage.this, "OTP sending failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                Intent intent = new Intent(SignupPage.this, OtpVerificationActivity.class);
                                intent.putExtra("verificationId", verificationId);
                                intent.putExtra("phone", phoneNumber);
                                intent.putExtra("sapid", sapId);
                                startActivity(intent);
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}