package com.example.seanippolito.geoquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEATER_BOOL = "cheated";
    private static final String ANSWERED_BOOL0 = "answered0";
    private static final String ANSWERED_BOOL1 = "answered1";
    private static final String ANSWERED_BOOL2 = "answered2";
    private static final String ANSWERED_BOOL3 = "answered3";
    private static final String ANSWERED_BOOL4 = "answered4";
    private static final String ANSWERED_BOOL5 = "answered5";
    private static final String TOTAL_ANSWERED_CORRECT = "correct_answers";
    private static final String CHEAT_ATTEMPTS_LEFT = "cheat_attempts_left";

    private static final int    REQUEST_CODE_CHEAT = 0;

    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private int question;
    private int mCurrentIndex = 0;
    private int totalCorrect = 0;
    private int percentCorrect = 0;
    private int mCheatAttemptsLeft = 3;
    private boolean mShowAnswerButtonVisibility = true;
    private boolean mIsCheater = false;
    private boolean mButtonIsEnabled = true;
    private boolean mAllAnswered = false;

    private final Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_mideast, false)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_button);
        ImageButton previousButton = (ImageButton) findViewById(R.id.previous_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(1);
            }
        });
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(1);
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(-1);
            }
        });
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                int cheatAttemptsLeft = mCheatAttemptsLeft;
                boolean showAnswerButtonVisibility = mShowAnswerButtonVisibility;

                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, cheatAttemptsLeft, showAnswerButtonVisibility);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(CHEATER_BOOL);
            mQuestionBank[0].setEnabled(savedInstanceState.getBoolean(ANSWERED_BOOL0));
            mQuestionBank[1].setEnabled(savedInstanceState.getBoolean(ANSWERED_BOOL1));
            mQuestionBank[2].setEnabled(savedInstanceState.getBoolean(ANSWERED_BOOL2));
            mQuestionBank[3].setEnabled(savedInstanceState.getBoolean(ANSWERED_BOOL3));
            mQuestionBank[4].setEnabled(savedInstanceState.getBoolean(ANSWERED_BOOL4));
            mQuestionBank[5].setEnabled(savedInstanceState.getBoolean(ANSWERED_BOOL5));
            totalCorrect = savedInstanceState.getInt(TOTAL_ANSWERED_CORRECT);
            mQuestionBank[mCurrentIndex].setCheater(mIsCheater);
            mCheatAttemptsLeft = savedInstanceState.getInt(CHEAT_ATTEMPTS_LEFT);
            if(mCheatAttemptsLeft <= 0) {
                mCheatButton.setEnabled(false);
            }
        }

        updateQuestion(0);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(CHEATER_BOOL, mIsCheater);
        savedInstanceState.putBoolean(ANSWERED_BOOL0, mQuestionBank[0].isEnabled());
        savedInstanceState.putBoolean(ANSWERED_BOOL1, mQuestionBank[1].isEnabled());
        savedInstanceState.putBoolean(ANSWERED_BOOL2, mQuestionBank[2].isEnabled());
        savedInstanceState.putBoolean(ANSWERED_BOOL3, mQuestionBank[3].isEnabled());
        savedInstanceState.putBoolean(ANSWERED_BOOL4, mQuestionBank[4].isEnabled());
        savedInstanceState.putBoolean(ANSWERED_BOOL5, mQuestionBank[5].isEnabled());
        savedInstanceState.putInt(TOTAL_ANSWERED_CORRECT, totalCorrect);
        savedInstanceState.putInt(CHEAT_ATTEMPTS_LEFT, mCheatAttemptsLeft);

    }

    private void updateQuestion(int update) {
        Log.i(TAG, "updateQuestion called()");
        if(Math.abs(update) == 1) { mIsCheater = false; }

        if((mCurrentIndex + update) < 0) { mCurrentIndex = mQuestionBank.length; }

        mCurrentIndex = (mCurrentIndex + update) % mQuestionBank.length;

        if(mAllAnswered){
            mCurrentIndex = 0;
            mAllAnswered = false;
        }

        try {
            question = mQuestionBank[mCurrentIndex].getTextResId();
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(TAG, "Index was out of bounds", ex);
        }
        mQuestionTextView.setText(question);

        checkIfAnswered(mQuestionBank[mCurrentIndex].isEnabled());
        mShowAnswerButtonVisibility = true;
    }

    private void checkAnswer(boolean userPressedTrue) {
        Log.i(TAG, "checkAnswer() called");
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        boolean cheater = mQuestionBank[mCurrentIndex].isCheater();

        String messageResId;
        mAllAnswered = true;

        if(cheater){
            messageResId = getString(R.string.judgement_toast);
            percentCorrect = ((totalCorrect * 100) / mQuestionBank.length);
        } else {
            if (userPressedTrue == answerIsTrue) {
                totalCorrect++;
                percentCorrect = ((totalCorrect * 100) / mQuestionBank.length);
                messageResId = getString(R.string.correct_toast);
            } else {
                messageResId = getString(R.string.incorrect_toast);
            }
        }

        checkIfAnswered(false);

        mQuestionBank[mCurrentIndex].setEnabled(false);

        //check if all have been answered
        for (Question aMQuestionBank : mQuestionBank) {
            if (aMQuestionBank.isEnabled()) {
                mAllAnswered = false;
            }
        }

        if(mAllAnswered){
            if(cheater) {
                toastMessage(this, messageResId);
            }
            messageResId = getString(R.string.percent_correct_toast, (int) percentCorrect);
            toastMessage(this, messageResId);

            //start over
            for (Question aMQuestionBank : mQuestionBank) {
                aMQuestionBank.setEnabled(true);
                aMQuestionBank.setCheater(false);
            }
            totalCorrect = 0;
            updateQuestion(0);
            mCheatAttemptsLeft = 3;
            mCheatButton.setEnabled(true);
        } else {
            toastMessage(this, messageResId);
        }
    }

    private void toastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 1500);
        toast.show();
    }

    private void checkIfAnswered(boolean answered){
        Log.i(TAG, "checkIfAnswered() called");
        mButtonIsEnabled = answered;
        mTrueButton.setEnabled(answered);
        mFalseButton.setEnabled(answered);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(TAG, "onActivityResult() called");
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHEAT) {
            if(data != null){
                mIsCheater = CheatActivity.wasAnswerShown(data);
                mCheatAttemptsLeft = CheatActivity.cheatAttemptsLeft(data);
                mShowAnswerButtonVisibility = CheatActivity.showAnswerButtonVisibility(data);
                mQuestionBank[mCurrentIndex].setCheater(mIsCheater);
                mQuestionBank[mCurrentIndex].setEnabled(mButtonIsEnabled);
                checkIfAnswered(mButtonIsEnabled);

                if(mCheatAttemptsLeft <= 0) {
                    mCheatButton.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "onStart() called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "onResume() called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause() called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy() called");
    }
}
