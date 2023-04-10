package com.example.smartprescriptionpadfordoctors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartprescriptionpadfordoctors.form.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(3*1000);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }catch (Exception e){

                   e.printStackTrace();
            }
            super.run();

    }
};thread.start();