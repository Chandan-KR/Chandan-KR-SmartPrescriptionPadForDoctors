package com.example.smartprescriptionpadfordoctors;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  private Button loginmainbtn;
  private Button signupbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            //login btn ---- navigating from first page to login page
            loginmainbtn =findViewById(R.id.loginmainbtn);
            loginmainbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,signin.class);
                    startActivity(intent);
                }
            });


            //signup btn id --- navigating first page to sign-up page
            signupbtn =findViewById(R.id.signupmainbtn);
            signupbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,signup.class);
                    startActivity(intent);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("something went roung in mainActivity","exception"+e);
        }

    }

}