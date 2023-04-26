package com.example.smartprescriptionpadfordoctors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class signIn extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginbutton;
    FirebaseAuth mAuth;
    TextView forgot_signin_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();


        loginEmail = findViewById(R.id.signin_email);
        loginPassword = findViewById(R.id.signin_password);
        loginbutton = findViewById(R.id.signin_button);
        forgot_signin_text = findViewById(R.id.forgot_signin_text);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(loginEmail.getText());
                String password = String.valueOf(loginPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(signIn.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(signIn.this, "Password email", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                if (user.isEmailVerified()) {
                                    Toast.makeText(signIn.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(signIn.this,main_nav.class);
                                    startActivity(intent);
                                } else {
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(signIn.this, "Kindly verify email", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(signIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }


                            }
                        });


            }
        });
        forgot_signin_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signIn.this,forgot.class);
                startActivity(intent);
            }
        });
    }
}