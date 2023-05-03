package com.example.smartprescriptionpadfordoctors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_password extends AppCompatActivity {
    EditText firstPassword;
    EditText confirmPassword;
    Button set_password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        if (user.isEmailVerified()) {
            // get new password from user input
            firstPassword = findViewById(R.id.firstPassword);
            confirmPassword = findViewById(R.id.confirmPassword);
            set_password = findViewById(R.id.set_password);
            String newPassword = String.valueOf(firstPassword.getText());
            String confirmPass = String.valueOf(confirmPassword.getText());

            set_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!newPassword.equals(confirmPass)) {
                        Toast.makeText(change_password.this, "password not matched", Toast.LENGTH_SHORT).show();
                    } else {
                        // update password
                        user.updatePassword(newPassword).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Password updated successfully
                                Toast.makeText(change_password.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(change_password.this, signIn.class);
                                startActivity(intent);
                            } else {
                                // Failed to update password
                                Toast.makeText(change_password.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            // Email is not verified
            Toast.makeText(change_password.this, "Email is not verified", Toast.LENGTH_SHORT).show();
        }
    }
}