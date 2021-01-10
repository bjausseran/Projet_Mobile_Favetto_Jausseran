package com.favetto.td_android_jausseran_favetto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

}