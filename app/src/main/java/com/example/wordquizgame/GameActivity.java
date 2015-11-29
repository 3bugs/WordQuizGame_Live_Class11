package com.example.wordquizgame;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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

        mQuestionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        mQuestionImageView = (ImageView) findViewById(R.id.questionImageView);
        mButtonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);

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

/*
        Collections.shuffle(mFileNameList);
        for (int i = 0; i < NUM_QUESTIONS_PER_QUIZ; i++) {
            mQuizWordList.add(mFileNameList.get(i));
        }
*/

        while (mQuizWordList.size() < NUM_QUESTIONS_PER_QUIZ) {
            int randomIndex = mRandom.nextInt(mFileNameList.size());
            String filename = mFileNameList.get(randomIndex);

            if (mQuizWordList.contains(filename) == false) {
                mQuizWordList.add(filename);
            }
        }

        Log.i(TAG, "*** ชื่อไฟล์คำถามที่สุ่มได้ ***");
        for (String filename : mQuizWordList) {
            Log.i(TAG, filename);
        }

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        mAnswerTextView.setText(null);

        String msg = String.format(
                "คำถาม %d จาก %d",
                mScore + 1,
                NUM_QUESTIONS_PER_QUIZ
        );
        mQuestionNumberTextView.setText(msg);

        mAnswerFileName = mQuizWordList.remove(0);

        loadQuestionImage();
        prepareChoiceWords();
    }

    private void loadQuestionImage() {
        String category = mAnswerFileName.substring(0, mAnswerFileName.indexOf('-'));
        String filePath = category + "/" + mAnswerFileName + ".png";

        AssetManager assets = getAssets();
        try {
            InputStream stream = assets.open(filePath);
            Drawable image = Drawable.createFromStream(stream, filePath);
            mQuestionImageView.setImageDrawable(image);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading file: " + filePath);
        }
    }

    private void prepareChoiceWords() {
        mChoiceWordList.clear();
        String answerWord = getWord(mAnswerFileName);

        while (mChoiceWordList.size() < mNumChoices) {
            int randomIndex = mRandom.nextInt(mFileNameList.size());
            String randomWord = getWord(mFileNameList.get(randomIndex));

            if (mChoiceWordList.contains(randomWord) == false &&
                    randomWord.equals(answerWord) == false) {
                mChoiceWordList.add(randomWord);
            }
        }

        int randomIndex = mRandom.nextInt(mChoiceWordList.size());
        mChoiceWordList.set(randomIndex, answerWord);

        Log.i(TAG, "*** คำศัพท์ตัวเลือกที่สุ่มได้ ***");
        for (String word : mChoiceWordList) {
            Log.i(TAG, word);
        }
    }

    private String getWord(String filename) {
        return filename.substring(filename.indexOf('-') + 1);
    }
}











