package com.example.fastestresponse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class FinalScore extends AppCompatActivity {

    TextView finalScore;
    Button restartGame;
    Button mainMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_final_score);

        finalScore = findViewById(R.id.finalScore);
        restartGame = findViewById(R.id.restart);
        mainMenu = findViewById(R.id.mainMenu);


        //retrieve score from final round and display in this activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int score = extras.getInt("score");
            finalScore.setText(String.valueOf(score));
        }

        //go back to game play
        restartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FinalScore.this, SinglePlayer.class);
                startActivity(i);
                finish();
            }
        });

        //go back to main menu
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FinalScore.this, MainActivity.class);
                startActivity(i);
                finish();
;            }
        });
    }
}