package com.favetto.td_android_jausseran_favetto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {

    // Nombre de joueurs présents sur le menu
    private int nbWaiting = 0;
    // Nombre de joueurs en jeu
    public int nbPlayers = 0;

    // Permet de charger le layout du jeu
    private Intent gameActivity;
    // Permet de charger le layout des règles
    private Intent rulesActivity;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.context = this;

        // Initialisation des intent pour l'affichage des différents layout
        gameActivity = new Intent(MenuActivity.this, MainActivity.class);
        rulesActivity = new Intent(getApplicationContext(), RulesActivity.class);

        // Initialisation des boutons
        setLaunchBtn();
        setRulesBtn();
        setQuitBtn();

        // Trouver combien de joueurs sont connectés
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //____________________________________________________________________________//
        //____________________  NOMBRE DE JOUEURS DANS LE MENU _______________________//
        //____________________________________________________________________________//

        final DatabaseReference myRefNb = database.getReference("nbWaiting");
        // Est exécuté une fois sur nbWaiting, pour récupérer le nombre de joueurs en attente
        myRefNb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                myRefNb.setValue(value + 1);
                nbWaiting = value + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });

        // Est exécuté à chaque fois que la valeur nbWaiting change dans Firebase
        myRefNb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nbWaiting = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });

        //____________________________________________________________________________//
        //_______________________  NOMBRE DE JOUEURS EN JEU __________________________//
        //____________________________________________________________________________//

        final DatabaseReference myRefNbPlayer = database.getReference("nbPlayers");
        // Est exécuté une fois sur nbPLayers, pour récupérer le nombre de joueurs en jeu
        myRefNbPlayer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nbPlayers = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });

        // Est exécuté à chaque fois que la valeur nbPlayer change dans Firebase
        myRefNbPlayer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nbPlayers = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });
    }

    // onStop retire le joueur du décompte Firebase
    // Si c'est le dernier joueur : clear table + reset first player
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbWaiting");
        myRefNb.setValue(nbWaiting - 1);
    }

    // onRestart ajoute le joueur au decompte Firebase
    @Override
    protected void onRestart() {
        super.onRestart();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefNb = database.getReference("nbWaiting");
            myRefNb.setValue(nbWaiting + 1);
        }

    // Initialisation de tous les boutons
    // Bouton permettant de lancer la partie
    private void setLaunchBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_launch);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nbPlayers < 2) startActivity(gameActivity);
                else AlertBuilder.displayTooManyPlayerAlert(context, nbWaiting);
            }
        });
    }

    // Bouton permettant d'ouvrir la page des règles
    private void setRulesBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_rules);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(rulesActivity);
            }
        });
    }

    // Bouton permettant de quitter la partie
    private void setQuitBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_quit);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {AlertBuilder.displayConfirmExitAlert(context, nbWaiting);}

        });
    }

    // Action au clique sur le bouton retour
    @Override
    public void onBackPressed() {
        AlertBuilder.displayConfirmExitAlert(context, nbWaiting);
    }


}