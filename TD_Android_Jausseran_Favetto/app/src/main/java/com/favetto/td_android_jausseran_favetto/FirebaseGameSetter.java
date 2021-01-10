package com.favetto.td_android_jausseran_favetto;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseGameSetter extends AppCompatActivity {
    String[] letters =new String[]{"a","b","c"};
    private int nbPlayers = 0;
    private int currentPlayer = 1;
    private int isPlayer = 0;
    private int boardGame[][] = new int[3][3];
    private Button buttons[][] = new Button[3][3];

    public void setTxt(TextView txtCurrPlayer, TextView txtIsPlayer, TextView txtInGame) {
        this.txtCurrPlayer = txtCurrPlayer;
        this.txtIsPlayer = txtIsPlayer;
        this.txtInGame = txtInGame;
    }
    private TextView txtCurrPlayer;
    private TextView txtIsPlayer;
    private TextView txtInGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Find how many players are connected
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        // Tourne à chaque fois que le valeur nbPlayers change dans Firebase
        myRefNb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                nbPlayers = value;
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
    public boolean CheckEnoughPlayer()
    {
        if (nbPlayers >= 2)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefCurr = database.getReference("currPlayer");
            myRefCurr.setValue(1);
            ClearTable();

            if (isPlayer <= 2) return true;
            else return false;
        }
        else return false;
    }
    public void updateCurrentPlayer(int value)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefCurr = database.getReference("currPlayer");
        myRefCurr.setValue(value);
    }
    public void updateNbPlayer(int value)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbPlayers");
        myRefNb.setValue(value);
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }
    public int getIsPlayer() {
        return isPlayer;
    }

    public void setIsPlayer(int isPlayer) {
        this.isPlayer = isPlayer;
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int[][] getBoardGame() {
        return boardGame;
    }

    public void setBoardGame(int[][] boardGame) {
        this.boardGame = boardGame;
    }
    public void setBtnOneByOne(Button btn, int letter, int num)
    {
        buttons[letter][num] = btn;
    }
    public int ConvertLetterToInt(String value)
    {
        //Convert the letter to an id
        int boardLetter = -1;
        for (int i = 0; i < letters.length; i++) {
            if (letters[i].equals(value.substring(0, 1))) boardLetter = i;
        }
        return boardLetter;
    }


}
