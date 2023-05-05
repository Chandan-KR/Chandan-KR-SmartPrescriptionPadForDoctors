package com.example.smartprescriptionpadfordoctors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_password extends AppCompatActivity {
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button updatePasswordButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

// Get references to the views
        passwordEditText = findViewById(R.id.firstPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        updatePasswordButton = findViewById(R.id.set_password);

// Set an onclick listener for the update password button
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new password and confirm password
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // Check if the password fields are empty
                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(change_password.this, "Please enter a new password.", Toast.LENGTH_SHORT).show();
                }
                // Check if the passwords match
                else if (!password.equals(confirmPassword)) {
                    Toast.makeText(change_password.this, "Passwords don't match. Please try again.", Toast.LENGTH_SHORT).show();
                }
                // Update the password
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(change_password.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(change_password.this, "Password update failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
