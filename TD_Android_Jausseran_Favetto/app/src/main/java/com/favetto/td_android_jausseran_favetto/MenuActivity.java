package com.favetto.td_android_jausseran_favetto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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

    // Nombre de joueurs en ligne
    private int nbWaiting = 0;
    private int nbPlayers = 0;
    private Intent gameActivity;
    private Intent rulesActivity;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.context = this;
        gameActivity = new Intent(MenuActivity.this, MainActivity.class);
        rulesActivity = new Intent(getApplicationContext(), RulesActivity.class);

        setLaunchBtn();
        setRulesBtn();
        setQuitBtn();


        // Find how many players are connected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbWaiting");
        //_________________________________NOMBRE DE JOUEUR_________________________//
        // Tourne une fois sur nbWaiting, pour récupérer le numero du joueurs
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

        // Tourne à chaque fois que le valeur nbWaiting change dans Firebase
        myRefNb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                nbWaiting = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });
        //____________________________________________________________________________//
        //_________________________________NOMBRE DE JOUEUR_________________________//
        final DatabaseReference myRefNbPlayer = database.getReference("nbPlayers");
        // Tourne une fois sur nbPLayers, pour récupérer le numero du joueurs
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

        // Tourne à chaque fois que le valeur nbWaiting change dans Firebase
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

    //OnPause retire le joueur de decompte Firebase
    //   Si c'est le derneir joueur : clear table + reset first player
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbWaiting");
        myRefNb.setValue(nbWaiting - 1);
    }
    //OnPause retire le joueur de decompte Firebase
    //   Si c'est le derneir joueur : clear table + reset first player
    @Override
    protected void onRestart() {
        super.onRestart();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefNb = database.getReference("nbWaiting");
            myRefNb.setValue(nbWaiting + 1);
        }


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

    private void ThrowTooMuchPlayerAlert() {

        String strToDisplay = "Trop de joueurs dans la partie. Veuillez réessayer plus tard. Il y a " +
                 + nbWaiting + " joueurs en attente.";

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Vous êtes de trop !");
        alertDialog.setMessage(strToDisplay);

        alertDialog.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    private void setQuitBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_quit);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {finish();}

        });
    }
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


}