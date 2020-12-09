package com.favetto.td_android_jausseran_favetto;

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

    private TextView txtPlayer;

    private ArrayList<Button> all_buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPlayer = findViewById(R.id.player);

        //Clear button (here for tests)
        Button btnClear = findViewById(R.id.buttonClear);

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

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(letter + num);
                        myRef.setValue(Integer.toString(currentPlayer));

                        //Changement de joueur
                        if (currentPlayer == 1) {
                            currentPlayer = 2;
                            txtPlayer.setText("O");
                        } else {
                            currentPlayer = 1;
                            txtPlayer.setText("X");
                        }
                    }
                });

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(letter + num);

                // Run everytime a firebase value is updated
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
                        Log.w("APPX", "Failed to red value", error.toException());
                    }
                });
            }
        }
    }
}

