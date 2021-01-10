package com.favetto.td_android_jausseran_favetto;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Tools {


    // fonction permettant de savoir qui a gagné si la partie est terminée
    // 0 : partie non finie
    // 1 : X
    // 2 : O
    // 3 : egalite
    public static int checkWinner(int[][] boardGame){

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

    // Fonction permettant de redémarrer la partie
    public static void resetGame(int[][] boardGame, String[] letters){

        for (int col = 0; col <= 2; col++) {
            for (int line = 0; line <= 2; line++) {
                boardGame[col][line] = 0;
            }
        }
        ClearTable(letters);
    }

    // Vide la table Firebase
    public static void ClearTable(String[] letters)
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
    @SuppressLint("SetTextI18n")
    public static void CheckEnoughPlayer(int nbPlayer, int isPlayer, String[] letters, TextView txtInGame)
    {
        if (nbPlayer >= 2)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefCurr = database.getReference("currPlayer");
            myRefCurr.setValue(1);
            Tools.ClearTable(letters);

            if (isPlayer <= 2) txtInGame.setText("En partie");
            else txtInGame.setText("En attente");
        }
        else txtInGame.setText("En attente");
    }

    @SuppressLint("SetTextI18n")
    public static void SetPlayerText(TextView txtIsPlayer, int value) {
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
