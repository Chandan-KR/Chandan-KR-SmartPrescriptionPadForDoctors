package com.example.smartprescriptionpadfordoctors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class signup extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //navigating signup activity to sign in by text view(already have an account?sign in)
        TextView txt = findViewById(R.id.already_ac_signin);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this,signin.class);
                startActivity(intent);
            }
        });
    }
}