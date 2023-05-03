package com.example.smartprescriptionpadfordoctors;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class forgot extends AppCompatActivity {
    private static final String TAG = "FirebaseEmailVerify";

        private EditText emailEditText;
        private Button resetPasswordButton;

        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot);

            mAuth = FirebaseAuth.getInstance();

            emailEditText = findViewById(R.id.email_field);
            resetPasswordButton = findViewById(R.id.send_otp_button);

            resetPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter your email address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                   else {
                     checkAndVerifyEmail(email);
                    }
                }
            });
        }
    public  void checkAndVerifyEmail(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

            if (isNewUser) {
                Toast.makeText(forgot.this,"New user,please signUp first",Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(forgot.this,"Email already exists.",Toast.LENGTH_SHORT).show();
                sendVerificationEmail();
            }
        });
    }
    private void sendVerificationEmail() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(forgot.this,"Verification email sent you can now reset your password",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(forgot.this,change_password.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(forgot.this,"Error sending verification email.",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(forgot.this,"No user found.",Toast.LENGTH_SHORT).show();

        }
    }
    }









