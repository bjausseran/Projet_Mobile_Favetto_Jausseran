package com.favetto.td_android_jausseran_favetto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Intent gameActivity;
    private Intent reglesActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameActivity = new Intent(MainActivity.this, MainActivity_Game.class);
        reglesActivity = new Intent(getApplicationContext(), MainActivity_Regles.class);
    }

    public void onClickHeberger (View v) {
        startActivity(gameActivity);
    }

    public void onClickRejoindre (View v) {
        startActivity(gameActivity);
    }

    public void onClickRegles (View v) {
        startActivity(reglesActivity);
    }

    public void onClickQuitter (View v) { System.exit(0);}
}