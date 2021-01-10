package com.favetto.td_android_jausseran_favetto;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseMenuSetter extends AppCompatActivity {
    private int nbPlayers = 0;
    private int nbWaiting = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Find how many players are connected
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNbWaiting = database.getReference("nbWaiting");

        // Tourne à chaque fois que le valeur nbWaiting change dans Firebase
        myRefNbWaiting.addValueEventListener(new ValueEventListener() {
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
        final DatabaseReference myRefNbPlayer = database.getReference("nbWaiting");

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
    public void updateNbWaiting(int value)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefNb = database.getReference("nbWaiting");
        myRefNb.setValue(nbWaiting + value);
    }

    public int getNbWaiting() {
        return nbWaiting;
    }

    public void setNbWaiting(int nbWaiting) {
        this.nbWaiting = nbWaiting;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }
}
