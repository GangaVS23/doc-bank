package com.example.docbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
       setContentView(R.layout.activity_main);
        Thread background = new Thread() {
            public void run() {
                try {

                    sleep(3*1000);


                    Intent i=new Intent(getApplicationContext(),LOGIN.class);
                    startActivity(i);


                    finish();
                } catch (Exception e) {
                }
            }
        };

        background.start();

    }
}