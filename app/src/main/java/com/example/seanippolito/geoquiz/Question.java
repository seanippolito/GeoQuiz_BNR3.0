package com.example.seanippolito.geoquiz;

/**
 * Created by seanippolito on 2/21/17.
 * This is the Question object to be used across QuizActivity
 */

class Question {

    private final int mTextResId;
    private final boolean mAnswerTrue;
    private boolean mEnabled;
    private boolean mCheater;

    public boolean isCheater() {
        return mCheater;
    }

    public void setCheater(boolean cheated) {
        mCheater = cheated;
    }

    public final boolean isEnabled() {
        return mEnabled;
    }

    public final void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public final int getTextResId() {
        return mTextResId;
    }

    public final boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public Question(int textResId, boolean answerTrue){
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mEnabled = true;
        mCheater = false;
    }
}
