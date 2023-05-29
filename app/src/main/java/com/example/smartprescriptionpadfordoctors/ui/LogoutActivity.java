package com.example.smartprescriptionpadfordoctors.ui;
import static android.content.ContentValues.TAG;

import com.example.smartprescriptionpadfordoctors.signIn;



import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.smartprescriptionpadfordoctors.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.nav_logout2) {
                // Call the logout method
                Logout();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void Logout() {
            Log.d(TAG, "Logout method called");
            // Add your logout logic here
            FirebaseAuth.getInstance().signOut();
            //Firestore.getInstance().clearPersistence();
            // For example, you can start the login activity and clear the back stack
            Intent intent = new Intent(this, signIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
}

