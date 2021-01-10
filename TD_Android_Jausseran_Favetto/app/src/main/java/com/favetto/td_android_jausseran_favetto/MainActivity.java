package com.favetto.td_android_jausseran_favetto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    String[] letters =new String[]{"a","b","c"};
    private int boardGame[][] = new int[3][3];
    private Button buttons[][] = new Button[3][3];
    private boolean hasRotate = false;
    private Context context;

    // Joueur actuel    1 : X
    //                  2 : O

    private TextView txtCurrPlayer;
    private TextView txtIsPlayer;
    private TextView txtInGame;

    private FirebaseGameSetter firebase;

    private ArrayList<Button> all_buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        txtCurrPlayer = findViewById(R.id.player);
        txtIsPlayer = findViewById(R.id.isPlayer);
        txtInGame = findViewById(R.id.inGame);
        firebase = new FirebaseGameSetter();
        firebase.setTxt(txtCurrPlayer, txtIsPlayer, txtInGame);
        ButtonSetter.setBackButton((Button) findViewById(R.id.btn_back), firebase, context);

        if(hasRotate) hasRotate = false;


        // Find how many players are connected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");


        //____________________________________________________________________________//
    //_________________________________NOMBRE DE JOUEUR_________________________//
        if (savedInstanceState == null) {
            // Tourne une fois sur nbPLayers, pour récupérer le numero du joueurs
            myRefNb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer value = dataSnapshot.getValue(Integer.class);
                   firebase.updateNbPlayer(value + 1);
                    firebase.setIsPlayer(value + 1);
                    firebase.setNbPlayers(value + 1);
                    if (firebase.CheckEnoughPlayer()) txtInGame.setText("En partie");
                        else txtInGame.setText("En attente");;
                    SetPlayerText(value + 1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("APPX", "Failed to read value", error.toException());
                }
            });
        }
        final DatabaseReference myRefCurr = database.getReference("currPlayer");
        //_________________________________JOUEUR ACTUEL_________________________//
        // Recupère le joueur actuel à chaque update de currPlayer dans Firebase
        myRefCurr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String place = dataSnapshot.getKey();
                Integer value = dataSnapshot.getValue(Integer.class);
                firebase.setCurrentPlayer(value);
                //Changement de joueur
                if (firebase.getCurrentPlayer() == 1) {
                    txtCurrPlayer.setText("X");
                } else {
                    firebase.setCurrentPlayer(2);
                    txtCurrPlayer.setText("O");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });

    //____________________________________________________________________________//
    //___________________________________FOR TEST_________________________________//
        //Clear button (here for tests)
        Button btnClear = findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearTable();
            }
        });

    //____________________________________________________________________________//
    //______________________________GRID BUTTON SETTER____________________________//
        // Set all button of the grid
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int id = getResources().getIdentifier("btn_" + letters[j] + i, "id", getPackageName());
                final int num = i;
                final String letter = letters[j];

                Log.d("DEBUG", "case number : " + letter + i);

                Button button = findViewById(id);
                //bt.setBackground(null); //Si API >= 16
                firebase.setBtnOneByOne(button, j, i-1);
                ButtonSetter.setGridButton(button, firebase, letter, num, myRefCurr);

        //---------------------------FIREBASE READING-------------------------//
                DatabaseReference myRef = database.getReference(letter + num);
                // Run everytime a firebase case is updated
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        String value = dataSnapshot.getValue(String.class);
                        Log.d("APPX", "Value is: " + value);
                        int id = getResources().getIdentifier("btn_" + place, "id", getPackageName());
                        Button but = findViewById(id);

                        //Convert the letter to an id
                        int boardLetter = firebase.ConvertLetterToInt(place);

                        //Affiche le pion
                        Drawable drawableJoueur;
                        if (value.equals("1")) {
                            drawableJoueur = ContextCompat.getDrawable(getApplicationContext(), R.drawable.x);
                            but.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16
                        }
                        else if (value.equals("2")) {
                            drawableJoueur = ContextCompat.getDrawable(getApplicationContext(), R.drawable.o);
                            but.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16
                        }
                        else but.setBackgroundDrawable(null);

                        int parse = Integer.parseInt(place.substring(1, 2));
                    //Update Board
                        if (boardLetter != -1
                                && Integer.parseInt(place.substring(1, 2)) < 4
                                && Integer.parseInt(place.substring(1, 2)) > 0
                                && !value.equals("") && (firebase.getCurrentPlayer() == 1 || firebase.getCurrentPlayer() == 2))
                        {
                            boardGame[boardLetter][Integer.parseInt(place.substring(1, 2)) - 1] = firebase.getCurrentPlayer();
                            int res = checkWinner();
                            if (res != 0) {
                            AlertBuilder.displayEndGameDialog(res, context);
                            resetGame();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("APPX", "Failed to read value", error.toException());
                    }
                });
            }
        }
    }

    //---------------------------CHECK WINNER-------------------------//
    // 0 : partie non fini
    // 1 : X
    // 2 : O
    // 3 : egalite
    private int checkWinner(){

        // on regarde si il y a un gagnant sur les colonnes
        for (int col = 0; col <= 2; col++) {
            if (boardGame[col][0] != 0 && boardGame[col][0] == boardGame[col][1] && boardGame[col][0] == boardGame[col][2])
                return boardGame[col][0];
        }

        // on regarde si il y a un gagnant sur les lignes
        for (int line = 0; line <= 2; line++){
            if (boardGame[0][line] != 0 && boardGame[0][line] == boardGame[1][line] && boardGame[0][line] == boardGame[2][line])
                return boardGame[0][line];
        }

        // on regarde si il y a un gagnant sur la diagonale haut/gauche -> bas/droit
        if (boardGame[0][0] != 0 && boardGame[0][0] == boardGame[1][1] && boardGame[0][0] == boardGame[2][2])
            return boardGame[0][0];

        // on regarde si il y a un gagnant sur la diagonale haut/droite -> bas/gauche
        if (boardGame[2][0] != 0 && boardGame[2][0] == boardGame[1][1] && boardGame[2][0] == boardGame[0][2])
            return boardGame[2][0];

        // Egalité
        boolean isFull = true;
        for (int col = 0; col <= 2; col++) {
            for (int line = 0; line <= 2; line++){
                if (boardGame[col][line] == 0) { // case
                    isFull = false;
                    break;
                }
            }
            if (!isFull)
                break;
        }
        if (isFull)
            return 3;

        // Partie non fini
        return 0;
    }

    // 0 : partie non fini
    // 1 : X
    // 2 : O
    // 3 : egalite

    private void resetGame(){

        for (int col = 0; col <= 2; col++) {
            for (int line = 0; line <= 2; line++) {
                boardGame[col][line] = 0;
            }
        }
        ClearTable();
    }
    // Vide la table Firebase
    private void ClearTable()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                DatabaseReference myRef = database.getReference(letters[j] + i);
                myRef.setValue("");
            }
        }
    }


    private void SetPlayerText(int value) {
        if (value == 1) {
            txtIsPlayer.setText("X");
            txtIsPlayer.setTextSize(30);
        }else if(value == 2){
            txtIsPlayer.setText("O");
            txtIsPlayer.setTextSize(30);
        } else {
            txtIsPlayer.setText("NONE");
            txtIsPlayer.setTextSize(10);
        }
    }


    //OnPause retire le joueur de decompte Firebase
    //   Si c'est le derneir joueur : clear table + reset first player
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefNb = database.getReference("nbPlayers");
            myRefNb.setValue(firebase.getNbPlayers() - 1);
            final DatabaseReference myRefCurr = database.getReference("currPlayer");
            myRefCurr.setValue(1);
        } else {
            hasRotate = true;
            AlertBuilder.displayConfirmExitAlert(context, firebase.getNbPlayers());
        }
    }

    private void setBackBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_back);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {AlertBuilder.displayConfirmExitAlert(context, firebase.getNbPlayers());}
        });
    }

    private void drawGridValue()
    {
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < letters.length; j++) {

                int id = getResources().getIdentifier("btn_" + letters[j] + Integer.toString(i), "id", getPackageName());
                Button but = findViewById(id);

                //Affiche le pion
                Drawable drawableJoueur;
                if (boardGame[j][i-1] == 1) {
                    drawableJoueur = ContextCompat.getDrawable(getApplicationContext(), R.drawable.x);
                    but.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16
                }
                else if (boardGame[j][i-1] == 2) {
                    drawableJoueur = ContextCompat.getDrawable(getApplicationContext(), R.drawable.o);
                    but.setBackgroundDrawable(drawableJoueur); // Utiliser view.setBackground(drawableJoueur); si API >= 16
                }
                else but.setBackgroundDrawable(null);

            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertBuilder.displayConfirmExitAlert(context, firebase.getNbPlayers());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putIntArray("boardA", boardGame[0]);
        savedInstanceState.putIntArray("boardB", boardGame[1]);
        savedInstanceState.putIntArray("boardC", boardGame[2]);
        savedInstanceState.putInt("curr", firebase.getCurrentPlayer());
        savedInstanceState.putInt("nbPlayers", firebase.getNbPlayers());
        savedInstanceState.putInt("isPlayer", firebase.getIsPlayer());
        savedInstanceState.putBoolean("hasRotate", hasRotate);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        boardGame[0] = savedInstanceState.getIntArray("boardA");
        boardGame[1] = savedInstanceState.getIntArray("boardB");
        boardGame[2] = savedInstanceState.getIntArray("boardC");
        firebase.setCurrentPlayer(savedInstanceState.getInt("curr"));
        firebase.setNbPlayers(savedInstanceState.getInt("nbPlayers"));
        firebase.setIsPlayer(savedInstanceState.getInt("isPlayer"));
        hasRotate = savedInstanceState.getBoolean("hasRotate");
        drawGridValue();

    }
}

