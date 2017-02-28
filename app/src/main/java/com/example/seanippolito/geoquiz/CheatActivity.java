package com.example.seanippolito.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String TAG = "CheatActivity";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.seanippolito.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.seanippolito.geoquiz.answer_shown";
    private static final String EXTRA_CHEAT_ATTEMPTS_LEFT = "com.example.seanippolito.geoquiz.cheat_attempts_left";
    private static final String EXTRA_SHOW_ANSWER_BUTTON_VISIBILITY = "com.example.seanippolito.geoquiz.show_answer_button_visibility";
    private static final String WAS_SHOWN = "was_shown";
    private static final String CHEAT_ATTEMPTS_LEFT = "cheat_attempts_left";
    private static final String SHOW_ANSWER_BUTTON_VISIBLE = "show_answer_button_visibility";

    private static int mCheatAttemptsLeft;
    private static boolean mShowAnswerButtonVisibility = true;

    private TextView mCheatAttemptsLeftTextView;
    private TextView mDeviceAPITextView;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private boolean mFromSavedInstance = false;
    private boolean mWasShown;
    private boolean mAnswerIsTrue;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int cheatAttemptsLeft, boolean showAnsButtonVis) {
        Log.i(TAG, "newIntent() called");
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEAT_ATTEMPTS_LEFT, cheatAttemptsLeft);
        intent.putExtra(EXTRA_SHOW_ANSWER_BUTTON_VISIBILITY, showAnsButtonVis);

        mCheatAttemptsLeft = intent.getIntExtra(EXTRA_CHEAT_ATTEMPTS_LEFT, 0);
        mShowAnswerButtonVisibility = intent.getBooleanExtra(EXTRA_SHOW_ANSWER_BUTTON_VISIBILITY, true);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        Log.i(TAG, "wasAnswerShown");
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int cheatAttemptsLeft(Intent result) {
        return result.getIntExtra(EXTRA_CHEAT_ATTEMPTS_LEFT, 0);
    }

    public static boolean showAnswerButtonVisibility(Intent result) {
        return result.getBooleanExtra(EXTRA_SHOW_ANSWER_BUTTON_VISIBILITY, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mDeviceAPITextView = (TextView) findViewById(R.id.device_api);
        mCheatAttemptsLeftTextView = (TextView) findViewById(R.id.attempts_left);

        mDeviceAPITextView.setText(getString(R.string.device_api, Build.VERSION.SDK_INT));

        mWasShown = false;
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if(!mShowAnswerButtonVisibility) {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        } else {
            mShowAnswerButton.setVisibility(View.VISIBLE);
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswerShownResult(true);
            }
        });

        if(savedInstanceState != null) {
            mWasShown = savedInstanceState.getBoolean(WAS_SHOWN);
            mCheatAttemptsLeft = savedInstanceState.getInt(CHEAT_ATTEMPTS_LEFT);
            mFromSavedInstance = true;
            //noinspection WrongConstant, This works
            mShowAnswerButton.setVisibility(savedInstanceState.getInt(SHOW_ANSWER_BUTTON_VISIBLE));
            setAnswerShownResult(mWasShown);
        }

        mCheatAttemptsLeftTextView.setText(getString(R.string.cheat_attempts, mCheatAttemptsLeft));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.i(TAG, "onSaveInstanceState() Called");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(WAS_SHOWN, mWasShown);
        savedInstanceState.putInt(CHEAT_ATTEMPTS_LEFT, mCheatAttemptsLeft);
        savedInstanceState.putInt(SHOW_ANSWER_BUTTON_VISIBLE, mShowAnswerButton.getVisibility());
    }

    private void setAnswerShownResult(boolean isAnswerShown){
        Log.i(TAG, "setAnswerShowResult() called");
        if(isAnswerShown) {
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
        }

        if(!mFromSavedInstance) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int cx = mShowAnswerButton.getWidth() / 2;
                int cy = mShowAnswerButton.getHeight() / 2;
                float radius = mShowAnswerButton.getWidth();
                Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mShowAnswerButton.setVisibility(View.INVISIBLE);
                    }
                });
                anim.start();
            } else{
                mShowAnswerButton.setVisibility(View.INVISIBLE);
            }


            mCheatAttemptsLeft--;
            mCheatAttemptsLeftTextView.setText(getString(R.string.cheat_attempts, mCheatAttemptsLeft));
        }

        mWasShown = isAnswerShown;
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_CHEAT_ATTEMPTS_LEFT, mCheatAttemptsLeft);
        data.putExtra(EXTRA_SHOW_ANSWER_BUTTON_VISIBILITY, false);
        setResult(RESULT_OK, data);

        mFromSavedInstance = false;
    }
}
