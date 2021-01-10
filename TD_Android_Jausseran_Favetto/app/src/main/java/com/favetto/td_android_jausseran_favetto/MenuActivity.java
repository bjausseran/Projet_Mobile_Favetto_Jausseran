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

    private Intent gameActivity;
    private Intent rulesActivity;
    private Context context;
    private FirebaseMenuSetter firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.context = this;
        gameActivity = new Intent(MenuActivity.this, MainActivity.class);
        rulesActivity = new Intent(getApplicationContext(), RulesActivity.class);
        firebase = new FirebaseMenuSetter();

        ButtonSetter.setLaunchButton((Button) findViewById(R.id.btn_launch), firebase , gameActivity, this);
        ButtonSetter.setRulesButton((Button) findViewById(R.id.btn_rules), rulesActivity, context);
        setQuitBtn();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        getNbWaitingOnce();
        getNbInGameOnce();
    }
    private void getNbWaitingOnce()
    {
        // Find how many players are connected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNbWaiting = database.getReference("nbWaiting");
        //_________________________________NOMBRE DE JOUEUR_________________________//
        // Tourne une fois sur nbWaiting, pour récupérer le numero du joueurs
        myRefNbWaiting.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                myRefNbWaiting.setValue(value + 1);
                firebase.setNbWaiting(value + 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });
    }

    private void getNbInGameOnce()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //_________________________________NOMBRE DE JOUEUR_________________________//
        final DatabaseReference myRefNbPlayer = database.getReference("nbPlayers");
        // Tourne une fois sur nbPLayers, pour récupérer le numero du joueurs
        myRefNbPlayer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebase.setNbPlayers(dataSnapshot.getValue(Integer.class));
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
        firebase.updateNbWaiting(-1);
    }
    //OnPause retire le joueur de decompte Firebase
    //   Si c'est le derneir joueur : clear table + reset first player
    @Override
    protected void onRestart() {
        super.onRestart();
        firebase.updateNbWaiting(1);
        }

    private void setQuitBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_quit);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {finish();}

        });
    }


}