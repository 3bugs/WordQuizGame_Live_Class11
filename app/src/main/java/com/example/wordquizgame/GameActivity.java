package com.example.wordquizgame;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private static final int NUM_QUESTIONS_PER_QUIZ = 3;

    private int mDifficulty;
    private int mNumChoices;

    private TextView mQuestionNumberTextView;
    private ImageView mQuestionImageView;
    private TableLayout mButtonTableLayout;
    private TextView mAnswerTextView;

    private ArrayList<String> mFileNameList = new ArrayList<>();
    private ArrayList<String> mQuizWordList = new ArrayList<>();
    private ArrayList<String> mChoiceWordList = new ArrayList<>();

    private String mAnswerFileName;
    private int mScore;
    private int mTotalGuesses;

    private Random mRandom = new Random();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();
        mDifficulty = i.getIntExtra("diff", 0);

        switch (mDifficulty) {
            case 0:
                mNumChoices = 2;
                break;
            case 1:
                mNumChoices = 4;
                break;
            case 2:
                mNumChoices = 6;
                break;
        }

        String msg = String.format(
                "Difficulty: %d, NumChoices: %d",
                mDifficulty,
                mNumChoices
        );
        Log.i(TAG, msg);

        getImageFileName();
    }

    private void getImageFileName() {
        String[] categories = {"animals", "body", "colors", "numbers", "objects"};
        AssetManager assets = getAssets();
        for (String cate : categories) {
            try {
                String[] filenames = assets.list(cate);

                for (String filename : filenames) {
                    mFileNameList.add(filename.replace(".png", ""));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error listing file names in " + cate);
            }
        }

        Log.i(TAG, "*** รายชื่อไฟล์ภาพทั้งหมด ***");
        for (String filename : mFileNameList) {
            Log.i(TAG, filename);
        }

        startQuiz();
    }

    private void startQuiz() {
        mScore = 0;
        mTotalGuesses = 0;
        mQuizWordList.clear();

        while (mQuizWordList.size() < NUM_QUESTIONS_PER_QUIZ) {
            int randomIndex = mRandom.nextInt(mFileNameList.size());
        }
    }
}
