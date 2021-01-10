package com.favetto.td_android_jausseran_favetto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class RulesActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        // Initialisation du bouton retour
        setBackBtn();
        this.context = this;
    }

    // Bouton permettant de retourner au menu principal
    private void setBackBtn()
    {
        Button launchBtn = (Button) findViewById(R.id.btn_back_rules);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {AlertBuilder.displayConfirmReturnRulesAlert(context);}
        });
    }

    // Action au clique sur le bouton retour
    @Override
    public void onBackPressed() {
        AlertBuilder.displayConfirmReturnRulesAlert(context);
    }



}