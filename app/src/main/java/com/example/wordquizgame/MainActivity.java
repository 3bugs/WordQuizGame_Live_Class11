package com.example.wordquizgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button playGameButton, highScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playGameButton = (Button) findViewById(R.id.playGameButton);
        highScoreButton = (Button) findViewById(R.id.highScoreButton);

        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(R.string.choose_diff_label);

                String[] diffLabels = new String[]{"ง่าย", "ปานกลาง", "ยาก"};

                dialog.setItems(diffLabels, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "คุณเลือก: " + which);
                    }
                });

                dialog.show();
/*
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                i.putExtra("user", "Promlert");
                i.putExtra("age", 41);
                startActivity(i);
*/
            }
        });

        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        MainActivity.this,
                        "Android",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
