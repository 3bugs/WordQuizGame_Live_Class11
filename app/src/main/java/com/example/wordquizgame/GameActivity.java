package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wordquizgame.db.DatabaseHelper;

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

    private Animation shakeAnimation;

    private DatabaseHelper mHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        Music.play(this, R.raw.game);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Music.stop();
    }

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

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        shakeAnimation.setRepeatCount(3);

        mHelper = new DatabaseHelper(this);
        mDatabase = mHelper.getWritableDatabase();

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
        createChoiceButtons();
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

    private void createChoiceButtons() {
        for (int row = 0; row < mButtonTableLayout.getChildCount(); row++) {
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);
            tr.removeAllViews();
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        for (int row = 0; row < mNumChoices / 2; row++) {
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);

            for (int column = 0; column < 2; column++) {
                Button guessButton = (Button) inflater.inflate(
                        R.layout.guess_button,
                        tr,
                        false
                );

                guessButton.setText(mChoiceWordList.remove(0));
                guessButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitGuess((Button) v);
                    }
                });
                tr.addView(guessButton);
            }
        }
    }

    private void submitGuess(Button button) {
        String guessWord = button.getText().toString();
        String answerWord = getWord(mAnswerFileName);

        mTotalGuesses++;

        // ตอบถูก
        if (guessWord.equals(answerWord)) {
            mScore++;

            MediaPlayer mp = MediaPlayer.create(this, R.raw.applause);
            mp.setVolume(0.5f, 0.5f);
            mp.start();

            String msg = guessWord + " ถูกต้องนะคร้าบบ";
            mAnswerTextView.setText(msg);
            mAnswerTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            disableAllButtons();

            // ตอบถูก และเล่นครบทุกข้อแล้ว (จบเกม)
            if (mScore == NUM_QUESTIONS_PER_QUIZ) {

                saveScore();

                String msgResult = String.format(
                        "จำนวนครั้งที่ทาย: %d\nเปอร์เซ็นต์ความถูกต้อง: %.1f",
                        mTotalGuesses,
                        (100 * NUM_QUESTIONS_PER_QUIZ) / (double) mTotalGuesses
                );

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("สรุปผล");
                dialog.setMessage(msgResult);
                dialog.setCancelable(false);
                dialog.setPositiveButton("เริ่มเกมใหม่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startQuiz();
                    }
                });
                dialog.setNegativeButton("กลับหน้าหลัก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog.show();
            }
            // ตอบถูก แต่ยังเล่นไม่ครบทุกข้อ
            else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextQuestion();
                    }
                }, 2000);
            }
        }
        // ตอบผิด
        else {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.fail3);
            mp.start();

            mQuestionImageView.startAnimation(shakeAnimation);
            button.startAnimation(shakeAnimation);

            String msg = "ผิดครับ ลองใหม่นะครับ";
            mAnswerTextView.setText(msg);
            mAnswerTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            button.setEnabled(false);
        }
    }

    private void saveScore() {
        ContentValues cv = new ContentValues();
        double percent = (100 * NUM_QUESTIONS_PER_QUIZ) / (double) mTotalGuesses;
        cv.put(DatabaseHelper.COL_SCORE, percent);
        cv.put(DatabaseHelper.COL_DIFFICULTY, mDifficulty);

        mDatabase.insert(DatabaseHelper.TABLE_NAME, null, cv);
    }

    private void disableAllButtons() {
        for (int row = 0; row < mButtonTableLayout.getChildCount(); row++) {
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);

            for (int column = 0; column < tr.getChildCount(); column++) {
                Button b = (Button) tr.getChildAt(column);
                b.setEnabled(false);
            }
        }
    }

    private String getWord(String filename) {
        return filename.substring(filename.indexOf('-') + 1);
    }
}











