package com.example.smartprescriptionpadfordoctors.ui;
import com.example.smartprescriptionpadfordoctors.signIn;



import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartprescriptionpadfordoctors.R;
import com.google.firebase.auth.FirebaseAuth;


public class logout extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // Perform logout operation
        logout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_nav, menu);
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle logout click
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear any user session or authentication data
        // Example: Clear shared preferences, remove tokens, etc.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        // Redirect to the login screen
        Intent intent = new Intent(this,signIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



}
