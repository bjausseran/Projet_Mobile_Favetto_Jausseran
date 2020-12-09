package com.favetto.td_android_jausseran_favetto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String[] letters =new String[]{"a","b","c"};
    private int boardGame[][] = new int[3][3];

    // 1 : X
    // 2 : O
    private int currentPlayer = 1;
    private int isPlayer = 0;

    private TextView txtPlayer;
    private TextView txtIsPlayer;

    private ArrayList<Button> all_buttons = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        myRefNb.setValue(isPlayer - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPlayer = findViewById(R.id.player);
        txtIsPlayer = findViewById(R.id.isPlayer);

        //Clear button (here for tests)
        Button btnClear = findViewById(R.id.buttonClear);

        // Find how many players are connected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        final DatabaseReference myRefcurr = database.getReference("currPlayer");




        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                for (int i = 1; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        DatabaseReference myRef = database.getReference(letters[j] + i);
                        myRef.setValue("");
                    }
                }
            }
        });
                // Set all button of the grid
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int id = getResources().getIdentifier("btn_" + letters[j] + i, "id", getPackageName());
                final int num = i;
                final String letter = letters[j];

                Log.d("DEBUG", "case number : " + letter + i);

                Button button = findViewById(id);
                //bt.setBackground(null); //Si API >= 16
                button.setBackground(null);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //On ne fait rien si la case cliqué n'est pas vide
                        if (view.getBackground() != null)
                            return;
                        if (currentPlayer != isPlayer)
                            return;

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(letter + num);
                        myRef.setValue(Integer.toString(currentPlayer));

                        //Changement de joueur
                        if (isPlayer == 1) {
                            myRefcurr.setValue(2);
                        } else {
                            myRefcurr.setValue(1);
                        }
                    }
                });

                // Run everytime time nbPlayers value is updated
                myRefcurr.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        Integer value = dataSnapshot.getValue(Integer.class);
                        currentPlayer = value;
                        //Changement de joueur
                        if (value == 1) {
                            currentPlayer = 1;
                            txtPlayer.setText("O");
                        } else {
                            currentPlayer = 2;
                            txtPlayer.setText("X");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("APPX", "Failed to read value", error.toException());
                    }
                });

                DatabaseReference myRef = database.getReference(letter + num);

                // Run one time on nbPlayers value
                myRefNb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        Integer value = dataSnapshot.getValue(Integer.class);
                        myRefNb.setValue(value + 1);
                        isPlayer = value +1;
                        if (value + 1 == 1) {
                            txtIsPlayer.setText("X");
                        } else {
                            txtIsPlayer.setText("O");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("APPX", "Failed to read value", error.toException());
                    }
                });
                // Run everytime time nbPlayers value is updated
                myRefNb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        Integer value = dataSnapshot.getValue(Integer.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("APPX", "Failed to read value", error.toException());
                    }
                });

                // Run everytime a firebase case value is updated
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        String value = dataSnapshot.getValue(String.class);
                        Log.d("APPX", "Value is: " + value);
                        int id = getResources().getIdentifier("btn_" + place, "id", getPackageName());
                        Button but = findViewById(id);

                        //Affiche le pion
                        Drawable drawableJoueur;
                        if (value == "1") {
                            drawableJoueur = ContextCompat.getDrawable(getApplicationContext(), R.drawable.x);
                            but.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16
                        }
                        else if (value == "2") {
                            drawableJoueur = ContextCompat.getDrawable(getApplicationContext(), R.drawable.o);
                            but.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16
                        }
                        else but.setBackgroundDrawable(null);
                    //Convert the letter to an id
                        int boardLetter = -1;
                        for (int i = 0; i < letters.length; i++) {
                            if (letters[i] == place.substring(0, 1)) boardLetter = i;
                        }
                    //Update Board
                        if (boardLetter != -1) boardGame[boardLetter][Integer.parseInt(place.substring(1, 2))] = currentPlayer;
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("APPX", "Failed to read value", error.toException());
                    }
                });
            }
        }
    }
}

