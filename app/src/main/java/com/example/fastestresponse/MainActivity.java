package com.example.fastestresponse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button singlePlayer;
    private WhichColour colourTypes;
    private AlertDialog dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove toolbar and activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        singlePlayer = findViewById(R.id.single);


        colourTypes = WhichColour.getInstance(getApplicationContext());

        dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.color_chooser, null);

        final ImageView arrBlue = dialogView.findViewById(R.id.arrBlue);
        final ImageView arrRed = dialogView.findViewById(R.id.arrRed);
        final ImageView arrGreen = dialogView.findViewById(R.id.arrGreen);
        final ImageView arrGreyish = dialogView.findViewById(R.id.arrGrey);

        //set singlePlayer onclick listener to handle event.
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Add onClick listeners to set colour of arrow and start the SinglePlayer Game
                //activity
                arrBlue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        colourTypes.setIndex(WhichColour.COLOUR_BLUE);
                        startGameActivity();
                        dialogBuilder.dismiss();
                    }
                });

                arrRed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        colourTypes.setIndex(WhichColour.COLOUR_RED);
                        startGameActivity();
                        dialogBuilder.dismiss();
                    }
                });

                arrGreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        colourTypes.setIndex(WhichColour.COLOUR_GREEN);
                        startGameActivity();
                        dialogBuilder.dismiss();
                    }
                });

                arrGreyish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        colourTypes.setIndex(WhichColour.COLOUR_GREYISH);
                        startGameActivity();
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        //Just stay on the same page
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();

            }
        });

    }

    private void startGameActivity() {
        Intent i = new Intent(MainActivity.this, SinglePlayer.class);
        startActivity(i);
    }
}