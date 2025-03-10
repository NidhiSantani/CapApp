package com.example.capstone_kitchen;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignupPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        // Change color for "Terms & Conditions" and "Privacy Policy"
        TextView termsConditions = findViewById(R.id.termsConditions);
        String termsText = "By signing up you agree to our Terms &\nConditions and Privacy Policy";
        SpannableString spannableTerms = new SpannableString(termsText);
        spannableTerms.setSpan(new ForegroundColorSpan(Color.RED), 32, 49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTerms.setSpan(new ForegroundColorSpan(Color.RED), 54, 68, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsConditions.setText(spannableTerms);

        // Change color for "Log in"
        TextView signupText = findViewById(R.id.signupText);
        String loginText = "Already have an account? Log in";
        SpannableString spannableLogin = new SpannableString(loginText);
        spannableLogin.setSpan(new ForegroundColorSpan(Color.RED), 25, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setText(spannableLogin);
    }
}