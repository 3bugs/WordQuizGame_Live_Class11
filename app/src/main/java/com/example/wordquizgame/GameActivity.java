package com.example.wordquizgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();
        String user = i.getStringExtra("user");
        int age = i.getIntExtra("age", 0);

        String msg = String.format(
                "user: %s, age: %d",
                user,
                age
        );

        Log.i("GameActivity", msg);
    }
}
