package com.favetto.td_android_jausseran_favetto;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ButtonSetter {
    static public void setLaunchButton(Button btn, final FirebaseMenuSetter firebase, final Intent gameActivity, final Context context)
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebase.getNbPlayers() < 2) context.startActivity(gameActivity);
                else AlertBuilder.displayTooManyPlayerAlert(context, firebase.getNbWaiting());
            }
        });
    }
    static  public void setQuitButton(Button btn, final Context context)
    {
        //btn.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {finish();}

        //});
    }
    static  public void setRulesButton(Button btn, final Intent rulesActivity, final Context context)
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(rulesActivity);
            }
        });
    }

    static public  void setGridButton(Button btn, final FirebaseGameSetter firebase, final String letter, final int num, final DatabaseReference ref)
    {

        btn.setBackground(null);

        //------------------------------ON CLICK LISTENER-------------------------//
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //On ne fait rien si la case cliqué n'est pas vide
                if (view.getBackground() != null || firebase.getCurrentPlayer() != firebase.getIsPlayer() || firebase.getNbPlayers() < 2)
                    return;

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(letter + num);
                myRef.setValue(Integer.toString(firebase.getIsPlayer()));

                //Changement de joueur
                if (firebase.getIsPlayer() == 1) ref.setValue(2);
                else ref.setValue(1);

            }
        });
    }
    static public void setBackButton(Button btn, final FirebaseGameSetter firebase, final Context context )
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {AlertBuilder.displayConfirmExitAlert(context, firebase.getNbPlayers());}
        });
    }
}
