package com.favetto.td_android_jausseran_favetto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {

    // création de la grille
    String[] letters =new String[]{"a","b","c"};
    private final int[][] boardGame = new int[3][3];

    private boolean hasRotate = false;
    private Context context;

    // Joueur actuel    1 : X
    //                  2 : O
    private int currentPlayer = 1;
    // Numéro du joueurs (1 ou 2)
    private int isPlayer = 0;
    // Nombre de joueurs en ligne (max 2)
    private int nbPlayer = 0;

    // X ou O
    private TextView txtCurrPlayer;
    private TextView txtIsPlayer;
    // "En partie" ou "En attente"
    private TextView txtInGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation du bouton retour
        setBackBtn();

        this.context = this;
        if(hasRotate) hasRotate = false;

        txtCurrPlayer = findViewById(R.id.player);
        txtIsPlayer = findViewById(R.id.isPlayer);
        txtInGame = findViewById(R.id.inGame);

        // Récupère le nombre de joueurs connectés
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        final DatabaseReference myRefcurr = database.getReference("currPlayer");

    //____________________________________________________________________________//
    //_______________________________ JOUEUR ACTUEL ______________________________//
    // ____________________________________________________________________________//

        // Recupère le joueur actuel à chaque update de currPlayer dans Firebase
        myRefcurr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPlayer = dataSnapshot.getValue(Integer.class);
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
    //_____________________________ NOMBRE DE JOUEURS _____________________________//
    //____________________________________________________________________________//

        if (savedInstanceState == null) {
            // Tourne une fois sur nbPLayers, pour récupérer le numero du joueurs
            myRefNb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer value = dataSnapshot.getValue(Integer.class);
                    myRefNb.setValue(value + 1);
                    isPlayer = value + 1;
                    nbPlayer = value + 1;

                    Tools.CheckEnoughPlayer(nbPlayer, isPlayer, letters, txtInGame);
                    Tools.SetPlayerText(txtIsPlayer, value + 1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("APPX", "Failed to read value", error.toException());
                }
            });
        }

        // Tourne à chaque fois que le valeur nbPlayers change dans Firebase
        myRefNb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nbPlayer = dataSnapshot.getValue(Integer.class);
                Tools.CheckEnoughPlayer(nbPlayer, isPlayer, letters, txtInGame);
                //Si    numero joueur > nombre joueurs
                //      numero joueur = nombre joueurs = dernier joueur
                if (isPlayer > nbPlayer) {
                    Tools.SetPlayerText(txtIsPlayer, isPlayer);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("APPX", "Failed to read value", error.toException());
            }
        });

    //____________________________________________________________________________//
    //_____________________________  POUR TESTS  _________________________________//
    //____________________________________________________________________________//

        //Clear button, permet de nettoyer la grille pour faire différents tests
        Button btnClear = findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.ClearTable(letters);
            }
        });

    //____________________________________________________________________________//
    //________________ INITIALISATION DES BOUTONS DE LA GRILLE ___________________//
    //____________________________________________________________________________//

        // Initialise tous les boutons de la grille
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

                        //On ne fait rien si la case cliquée n'est pas vide
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
                // Exécuté chaque fois qu'une case firebase est mise à jour
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        String value = dataSnapshot.getValue(String.class);
                        Log.d("APPX", "Value is: " + value);
                        int id = getResources().getIdentifier("btn_" + place, "id", getPackageName());
                        Button but = findViewById(id);

                        // converti la lettre en un identifiant
                        int boardLetter = -1;
                        for (int i = 0; i < letters.length; i++) {
                            if (letters[i].equals(place.substring(0, 1))) boardLetter = i;
                        }

                        // Affiche le pion (X ou O)
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

                    // Met à jour le plateau
                        if (boardLetter != -1
                                && Integer.parseInt(place.substring(1, 2)) < 4
                                && Integer.parseInt(place.substring(1, 2)) > 0
                                && !value.equals("") && (currentPlayer == 1 || currentPlayer == 2))
                        {
                            boardGame[boardLetter][Integer.parseInt(place.substring(1, 2)) - 1] = currentPlayer;
                            int res = Tools.checkWinner(boardGame);
                            if (res != 0) {

                            AlertBuilder.displayEndGameDialog(res, context);
                            Tools.resetGame(boardGame, letters);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("APPX", "Failed to read value", error.toException());
                    }
                });
            }
        }
    }

    // Fonction permettant d'initialiser la grille
    private void drawGridValue()
    {
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < letters.length; j++) {

                int id = getResources().getIdentifier("btn_" + letters[j] + i, "id", getPackageName());
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

    // Initialisation des différents boutons

    //OnDestroy retire le joueur de decompte Firebase
    //   Si c'est le dernier joueur : clear table + reset first player
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefNb = database.getReference("nbPlayers");
            myRefNb.setValue(nbPlayer - 1);
            final DatabaseReference myRefCurr = database.getReference("currPlayer");
            myRefCurr.setValue(1);
        } else {
            hasRotate = true;
            AlertBuilder.displayConfirmReturnGameAlert(context, nbPlayer);
        }
    }

    // Bouton permettant de revenir au menu
    private void setBackBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_back);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {AlertBuilder.displayConfirmReturnGameAlert(context, nbPlayer);}
        });
    }

    // Action quand on clique sur le bouton retour
    @Override
    public void onBackPressed() {
        AlertBuilder.displayConfirmReturnGameAlert(context, nbPlayer);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putIntArray("boardA", boardGame[0]);
        savedInstanceState.putIntArray("boardB", boardGame[1]);
        savedInstanceState.putIntArray("boardC", boardGame[2]);
        savedInstanceState.putInt("curr", currentPlayer);
        savedInstanceState.putInt("nbPlayer", nbPlayer);
        savedInstanceState.putInt("isPlayer", isPlayer);
        savedInstanceState.putBoolean("hasRotate", hasRotate);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        boardGame[0] = savedInstanceState.getIntArray("boardA");
        boardGame[1] = savedInstanceState.getIntArray("boardB");
        boardGame[2] = savedInstanceState.getIntArray("boardC");
        currentPlayer = savedInstanceState.getInt("curr");
        nbPlayer = savedInstanceState.getInt("nbPlayer");
        isPlayer = savedInstanceState.getInt("isPlayer");
        hasRotate = savedInstanceState.getBoolean("hasRotate");
        drawGridValue();

    }
}

