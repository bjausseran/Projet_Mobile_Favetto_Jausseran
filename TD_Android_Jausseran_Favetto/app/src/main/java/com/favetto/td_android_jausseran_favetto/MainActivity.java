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
    private boolean hasStop = false;

    // Joueur actuel    1 : X
    //                  2 : O
    private int currentPlayer = 1;
    // Numéro du joueurs
    private int isPlayer = 0;
    // Nombre de joueurs en ligne
    private int nbPlayer = 0;

    private TextView txtCurrPlayer;
    private TextView txtIsPlayer;
    private TextView txtInGame;

    private ArrayList<Button> all_buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCurrPlayer = findViewById(R.id.player);
        txtIsPlayer = findViewById(R.id.isPlayer);
        txtInGame = findViewById(R.id.inGame);

        // Find how many players are connected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        final DatabaseReference myRefcurr = database.getReference("currPlayer");


    //_________________________________JOUEUR ACTUEL_________________________//
        // Recupère le joueur actuel à chaque update de currPlayer dans Firebase
        myRefcurr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String place = dataSnapshot.getKey();
                Integer value = dataSnapshot.getValue(Integer.class);
                currentPlayer = value;
                //Changement de joueur
                if (currentPlayer == 1) {
                    txtCurrPlayer.setText("X");
                } else {
                    currentPlayer = 2;
                    txtCurrPlayer.setText("O");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });
    //____________________________________________________________________________//
    //_________________________________NOMBRE DE JOUEUR_________________________//
        // Tourne une fois sur nbPLayers, pour récupérer le numero du joueurs
        myRefNb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                myRefNb.setValue(value + 1);
                isPlayer = value + 1;
                nbPlayer = value + 1;

                CheckEnoughPlayer();
                SetPlayerText(value + 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });

        // Tourne à chaque fois que le valeur nbPlayers change dans Firebase
        myRefNb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                nbPlayer = value;
                CheckEnoughPlayer();
                //Si    numero joueur > nombre joueurs
                //      numero joueur = nombre joueurs = dernier joueur
                if (isPlayer > isPlayer) {
                    SetPlayerText(isPlayer);
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
                button.setBackground(null);

        //------------------------------ON CLICK LISTENER-------------------------//
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //On ne fait rien si la case cliqué n'est pas vide
                        if (view.getBackground() != null || currentPlayer != isPlayer || nbPlayer < 2)
                            return;

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(letter + num);
                        myRef.setValue(Integer.toString(isPlayer));

                        //Changement de joueur
                        if (isPlayer == 1) myRefcurr.setValue(2);
                        else myRefcurr.setValue(1);

                    }
                });

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
                        int boardLetter = -1;
                        for (int i = 0; i < letters.length; i++) {
                            if (letters[i].equals(place.substring(0, 1))) boardLetter = i;
                        }

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
                                && !value.equals("") && (currentPlayer == 1 || currentPlayer == 2))
                        {
                            boardGame[boardLetter][Integer.parseInt(place.substring(1, 2)) - 1] = currentPlayer;
                            int res = checkWinner();
                            displayAlertDialog(res);
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

    //OnPause retire le joueur de decompte Firebase
    //   Si c'est le derneir joueur : clear table + reset first player
    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        myRefNb.setValue(nbPlayer - 1);
        if (nbPlayer == 1)
        {
            final DatabaseReference myRefCurr = database.getReference("currPlayer");
            myRefCurr.setValue(1);
            ClearTable();
        }
        hasStop = true;
    }
    //OnPause retire le joueur de decompte Firebase
    //   Si c'est le derneir joueur : clear table + reset first player
    @Override
    protected void onResume() {
        super.onResume();
        if (hasStop) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefNb = database.getReference("nbPlayers");
            myRefNb.setValue(nbPlayer + 1);
            isPlayer = nbPlayer +1;
            if (nbPlayer >= 2) {
                final DatabaseReference myRefCurr = database.getReference("currPlayer");
                myRefCurr.setValue(1);
                ClearTable();
            }
            hasStop = false;
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
    private void displayAlertDialog(int res){
        if (res == 0) // partie non termine
            return;

        String strToDisplay = "";
        if (res == 1)
            strToDisplay = "Les X ont gagnées !";
        if (res == 2)
            strToDisplay = "Les O ont gagnés !";
        if (res == 3)
            strToDisplay = "Egalité !";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Fin de la partie");
        alertDialog.setMessage(strToDisplay);

        alertDialog.setNeutralButton("Recommencer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetGame();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

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

    // Verifie s'il y a assez de joueur et prépare la partie
    private void CheckEnoughPlayer()
    {
        if (nbPlayer >= 2)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefCurr = database.getReference("currPlayer");
            myRefCurr.setValue(1);
            ClearTable();

            if (isPlayer <= 2) txtInGame.setText("En partie");
            else txtInGame.setText("En attente");
        }
        else txtInGame.setText("En attente");
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
}

