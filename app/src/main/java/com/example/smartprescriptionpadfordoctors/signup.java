package com.example.smartprescriptionpadfordoctors;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signup extends AppCompatActivity {
    EditText signupEmail,signupPhone,signupUsername,signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        // Initialize UI elements
        signupEmail = findViewById(R.id.signup_Email);
        signupPhone = findViewById(R.id.signup_phone);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton  = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(signupEmail.getText());
                String phone =  String.valueOf(signupPhone.getText());
                String username  =  String.valueOf(signupUsername.getText());
                String password = String.valueOf(signupPassword.getText());
                boolean isvalidated = validateData(email,phone,username,password);
                if (!isvalidated){
                    Toast.makeText(signup.this,"check creadentials again",Toast.LENGTH_SHORT).show();
                }
                else {
                // Sign up the user with email and password using Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // User sign up successful
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification();
                                    if (user != null) {
                                        // Create a new user document in Cloud Firestore
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email);
                                        userMap.put("phone", phone);
                                        userMap.put("username", username);

                                        mFirestore.collection("users")
                                                .document(user.getUid())
                                                .set(userMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(signup.this, "User created successfully,check email to verify", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(signup.this, signIn.class);
                                                            startActivity(intent);

                                                        } else {
                                                            Toast.makeText(signup.this, "Failed to create user document in Firestore", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // User sign up failed
                                    Toast.makeText(signup.this, "Failed to create user: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            }

        });

    }
    boolean validateData(String email,String phone,String username,String password) {
        //validate the data that are input by user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupEmail.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            signupPassword.setError("Password length must be greater than 6");
            return false;
        }
        if (username.length() == 0) {
            signupUsername.setError("User name is empty");
            return false;
        }
        if (phone.length() == 0 || phone.length()>10) {
            signupPhone.setError("Phone number is not valid");
            return false;
        }
        return true;
    }
}




