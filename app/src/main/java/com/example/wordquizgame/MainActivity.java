package com.example.wordquizgame;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playGameButton = (Button) findViewById(R.id.playGameButton);
        Button highScoreButton = (Button) findViewById(R.id.highScoreButton);

        MyListener listener = new MyListener();

        playGameButton.setOnClickListener(listener);
        highScoreButton.setOnClickListener(listener);
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == R.id.playGameButton) {
                Toast t = Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT);
                t.show();
            } else if (id == R.id.highScoreButton) {
                Toast t = Toast.makeText(MainActivity.this, "Android", Toast.LENGTH_SHORT);
                t.show();
            }

        }
    }
}
