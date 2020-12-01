package com.favetto.td_android_jausseran_favetto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int boardGame[][] = new int[3][3];

    // 1 : X
    // 2 : O
    private int currentPlayer = 1;

    private TextView txtPlayer;

    private ArrayList<Button> all_buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPlayer = findViewById(R.id.player);

        // récupération des différents identifiants des boutons
        Button bt1 = findViewById(R.id.btn_1);
        Button bt2 = findViewById(R.id.btn_2);
        Button bt3 = findViewById(R.id.btn_3);
        Button bt4 = findViewById(R.id.btn_4);
        Button bt5 = findViewById(R.id.btn_5);
        Button bt6 = findViewById(R.id.btn_6);
        Button bt7 = findViewById(R.id.btn_7);
        Button bt8 = findViewById(R.id.btn_8);
        Button bt9 = findViewById(R.id.btn_9);

        all_buttons.add(bt1);
        all_buttons.add(bt2);
        all_buttons.add(bt3);
        all_buttons.add(bt4);
        all_buttons.add(bt5);
        all_buttons.add(bt6);
        all_buttons.add(bt7);
        all_buttons.add(bt8);
        all_buttons.add(bt9);

        for (Button bt : all_buttons) {
            //bt.setBackground(null); //Si API >= 16
            bt.setBackground(null);
            bt.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        //On ne fait rien si la case cliqué n'est pas vide
        if (view.getBackground() != null)
            return;

        //Gestion du plateau
        switch (view.getId()) {
            case R.id.btn_1:     // if(view.getId() == R.id.bt1)
                boardGame[0][0] = currentPlayer;
                break;
            case R.id.btn_2:
                boardGame[1][0] = currentPlayer;
                break;
            case R.id.btn_3:
                boardGame[2][0] = currentPlayer;
                break;
            case R.id.btn_4:
                boardGame[0][1] = currentPlayer;
                break;
            case R.id.btn_5:
                boardGame[1][1] = currentPlayer;
                break;
            case R.id.btn_6:
                boardGame[2][1] = currentPlayer;
                break;
            case R.id.btn_7:
                boardGame[0][2] = currentPlayer;
                break;
            case R.id.btn_8:
                boardGame[1][2] = currentPlayer;
                break;
            case R.id.btn_9:
                boardGame[2][2] = currentPlayer;
                break;
            default:
                return;
        }

        //Affiche le pion
        Drawable drawableJoueur;
        if (currentPlayer == 1)
            drawableJoueur = ContextCompat.getDrawable(this, R.drawable.x);
        else
            drawableJoueur = ContextCompat.getDrawable(this, R.drawable.o);
        view.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16

        //Changement de joueur
        if (currentPlayer == 1) {
            currentPlayer = 2;
            txtPlayer.setText("O");
        } else {
            currentPlayer = 1;
            txtPlayer.setText("X");
        }
    }
}
