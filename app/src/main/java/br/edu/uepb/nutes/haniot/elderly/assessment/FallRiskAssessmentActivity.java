package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import br.edu.uepb.nutes.haniot.R;

/**
 * FallRiskAssessmentActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class FallRiskAssessmentActivity extends AppIntro implements OnAnswerListener {
    private final String TAG = "FallRiskAssActivity";

    public static final String KEY_QUESTIONS = "key_questions";
    public static final String KEY_ANSWERS = "key_answers";

    private final int PAGE_1 = 0;
    private final int PAGE_2 = 1;
    private final int PAGE_3 = 2;
    private final int PAGE_4 = 3;
    private final int PAGE_5 = 4;
    private final int PAGE_6 = 5;
    private final int PAGE_7 = 6;
    private final int PAGE_8 = 7;
    private final int PAGE_9 = 8;
    private final int PAGE_10 = 9;
    private final int PAGE_END = 10;

    private String[] questions;
    private boolean[] answers;
    private SliderPageFragment currentPage;


    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions = getResources().getStringArray(R.array.risk_questions_array);
        answers = new boolean[10];

        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(true);
        showSkipButton(true);
        setSkipText(getString(R.string.cancel_text));
        setNextPageSwipeLock(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group1),
                getString(R.string.risk_fall_description_q1),
                R.drawable.fall_elderly,
                ContextCompat.getColor(this, R.color.colorPink),
                PAGE_1));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group2),
                getString(R.string.risk_fall_description_q2),
                R.drawable.walker_elderly,
                ContextCompat.getColor(this, R.color.colorPurple),
                PAGE_2));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group3),
                getString(R.string.risk_fall_description_q3),
                R.drawable.medications_elderly,
                ContextCompat.getColor(this, R.color.colorLightBlue),
                PAGE_3));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group3),
                getString(R.string.risk_fall_description_q4),
                R.drawable.medications_elderly,
                ContextCompat.getColor(this, R.color.colorLightBlue),
                PAGE_4));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group4),
                getString(R.string.risk_fall_description_q5),
                R.drawable.coast_pain_elderly,
                ContextCompat.getColor(this, R.color.colorLightGreen),
                PAGE_5));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group5),
                getString(R.string.risk_fall_description_q6),
                R.drawable.daily_activity_elderly,
                ContextCompat.getColor(this, R.color.colorOrange),
                PAGE_6));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group6),
                getString(R.string.risk_fall_description_q7),
                R.drawable.difficulty_seeing_elderly,
                ContextCompat.getColor(this, R.color.colorBlueGrey),
                PAGE_7));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group6),
                getString(R.string.risk_fall_description_q8),
                R.drawable.hearing_elderly,
                ContextCompat.getColor(this, R.color.colorBlueGrey),
                PAGE_8));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group7),
                getString(R.string.risk_fall_description_q9),
                R.drawable.physical_activity_elderly,
                ContextCompat.getColor(this, R.color.colorIndigo),
                PAGE_9));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group8),
                getString(R.string.risk_fall_description_q10),
                R.drawable.abajur,
                ContextCompat.getColor(this, R.color.colorCyan),
                PAGE_10));

        addSlide(SliderPageFragment.newInstance(R.layout.fragment_elderly_fall_risk_end, PAGE_END));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Log.d(TAG, "onSkipPressed()" + currentFragment.getClass().getName());
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Log.d(TAG, "onDonePressed()" + currentFragment.getClass().getName());
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if (newFragment instanceof SliderPageFragment) {
            currentPage = (SliderPageFragment) newFragment;

            if (currentPage.getPageNumber() == PAGE_END) return;

            if (currentPage.isBlocked()) setNextPageSwipeLock(true);
            else setNextPageSwipeLock(false);

            if (currentPage.getOldCheckedRadio() == 0)
                currentPage.getRadioGroup().check(R.id.no_radioButton);
            else if (currentPage.getOldCheckedRadio() == 1)
                currentPage.getRadioGroup().check(R.id.yes_radioButton);
        }
    }

    @Override
    public boolean onCanRequestNextPage() {
        return super.onCanRequestNextPage();
    }

    @Override
    public void onAnswer(View view, boolean value, int page) {
        if (page < PAGE_END) {
            Log.d(TAG, "onAnswer() NOT END");
            answers[page] = value;
            return;
        }

        if (value) processAssessment();// End result
        else showMessageCancel();// Cancel result
    }

    /**
     * Process result assessment.
     */
    private void processAssessment() {
        Intent intent = new Intent(this, FallRiskAssessmentResultActivity.class);
        intent.putExtra(KEY_ANSWERS, answers);
        intent.putExtra(KEY_QUESTIONS, questions);
        startActivity(intent);

        finish();
    }

    /**
     * Show dialog mesage cancel.
     */
    private void showMessageCancel() {
        runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.risk_fall_title_cancel));

            dialog.setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                finish();
            });

            dialog.setNegativeButton(R.string.no_text, (dialogInterface, which) -> {
                if (currentPage != null)
                    currentPage.getRadioGroup().clearCheck();
            });

            dialog.create().show();
        });
    }

    /**
     * Show message page blocked.
     */
    private void showMessageBlocked() {
        final Snackbar snackbar = Snackbar.make(getCurrentFocus(),
                R.string.error_internal_device,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.bt_ok, (v) -> {
            snackbar.dismiss();
        });
        snackbar.show();
    }
}

