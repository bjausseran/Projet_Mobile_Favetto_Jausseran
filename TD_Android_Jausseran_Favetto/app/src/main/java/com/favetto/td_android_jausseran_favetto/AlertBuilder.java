package com.favetto.td_android_jausseran_favetto;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlertBuilder {

    // Message d'alerte quand on clique sur le bouton quitter ou sur la touche de retour
    // quand on se trouve sur la page du menu
    static public void displayConfirmExitAlert(final Context context, final int nbWaiting)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Voulez vous vraiment quitter ?")
                .setTitle("Attention !")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRefNb = database.getReference("nbWaiting");
                        myRefNb.setValue(nbWaiting - 1);

                        System.exit(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    // Message d'alerte quand on clique sur le bouton retour ou sur la touche de retour
    // quand on se trouve sur la page des règles
    static public void displayConfirmReturnRulesAlert(final Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Voulez vous vraiment retourner au menu principal ?")
                .setTitle("Attention !")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    // Message d'alerte quand on clique sur le bouton retour ou sur la touche de retour
    // quand on se trouve en jeu
    static public void displayConfirmReturnGameAlert(final Context context, final int nbPlayer)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Voulez vous vraiment retourner au menu principal ?")
                .setTitle("Attention !")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRefNb = database.getReference("nbPlayers");
                        myRefNb.setValue(nbPlayer - 1);

                        System.exit(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    // Message d'alerte quand le jeu se termine
    static public void displayEndGameDialog(int res, Context context){

        String strToDisplay = "";
        if (res == 1)
            strToDisplay = "Les X ont gagnées !";
        if (res == 2)
            strToDisplay = "Les O ont gagnés !";
        if (res == 3)
            strToDisplay = "Egalité !";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Fin de la partie");
        alertDialog.setMessage(strToDisplay);

        alertDialog.setNeutralButton("Recommencer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    // Message d'alerte quand un joueur essaie de se connecter alors qu'il y a déjà 2 personnes en jeu
    static public void displayTooManyPlayerAlert(Context context, int nbWaiting) {

        String strToDisplay = "Trop de joueurs dans la partie. Veuillez réessayer plus tard."
                            + "Il y a " + nbWaiting + " joueurs en attente.";

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Vous êtes de trop !");
        alertDialog.setMessage(strToDisplay);

        alertDialog.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
}
