package com.favetto.td_android_jausseran_favetto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    String[] letters =new String[]{"a","b","c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("Main", "Start Main TicTacToe");
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

        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int id = getResources().getIdentifier("btn_" + letters[j] + i, "id", getPackageName());
                final int num = i;
                final String letter = letters[j];

                Log.d("DEBUG", "case number : " +  letter + i);

                Button button = findViewById(id);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(letter + num);
                        myRef.setValue("X");
                    }
                });

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(letter + num);

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String place = dataSnapshot.getKey();
                        String value = dataSnapshot.getValue(String.class);
                        Log.d("APPX", "Value is: " + value);
                        int id = getResources().getIdentifier("btn_" + place, "id", getPackageName());
                        Button but = findViewById(id);
                        but.setText(value);
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

