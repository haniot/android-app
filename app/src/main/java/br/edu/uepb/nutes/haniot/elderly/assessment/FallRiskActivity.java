package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;

import java.util.Arrays;

import br.edu.uepb.nutes.haniot.R;

/**
 * FallRiskActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class FallRiskActivity extends AppIntro implements SliderPageFragment.OnResponseListener {
    private final String TAG = "FallRiskActivity";
    private final int PAGE_1 = 0;
    private final int PAGE_2 = 2;
    private final int PAGE_3 = 3;
    private final int PAGE_4 = 4;
    private final int PAGE_5 = 5;
    private final int PAGE_6 = 6;
    private final int PAGE_7 = 7;
    private final int PAGE_8 = 8;
    private final int PAGE_9 = 9;
    private final int PAGE_10 = 10;
    private final int PAGE_RES = 11;

    private boolean[] answers;
    private String[] questions;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answers = new boolean[10];
        questions = new String[10];

        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(false);
        showSkipButton(false);
        setNextPageSwipeLock(true);

        setImmersiveMode(true);

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
                R.drawable.walker,
                ContextCompat.getColor(this, R.color.colorPurple),
                PAGE_2));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group3),
                getString(R.string.risk_fall_description_q3),
                ContextCompat.getColor(this, R.color.colorLightBlue),
                PAGE_3));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group3),
                getString(R.string.risk_fall_description_q4),
                ContextCompat.getColor(this, R.color.colorLightBlue),
                PAGE_4));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group4),
                getString(R.string.risk_fall_description_q5),
                ContextCompat.getColor(this, R.color.colorLightGreen),
                PAGE_5));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group5),
                getString(R.string.risk_fall_description_q6),
                ContextCompat.getColor(this, R.color.colorOrange),
                PAGE_6));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group6),
                getString(R.string.risk_fall_description_q7),
                ContextCompat.getColor(this, R.color.colorBlueGrey),
                PAGE_7));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group6),
                getString(R.string.risk_fall_description_q8),
                ContextCompat.getColor(this, R.color.colorBlueGrey),
                PAGE_8));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group7),
                getString(R.string.risk_fall_description_q9),
                ContextCompat.getColor(this, R.color.colorIndigo),
                PAGE_9));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_risk,
                getString(R.string.risk_fall_title_group8),
                getString(R.string.risk_fall_description_q10),
                ContextCompat.getColor(this, R.color.colorCyan),
                PAGE_10));

        addSlide(SliderPageResultFragment.newInstance(
                R.layout.fragment_elderly_fall_risk_end,
                ContextCompat.getColor(this, R.color.colorPurple),
                PAGE_RES));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferencesEditor.clear().commit();
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
            SliderPageFragment currentPage = (SliderPageFragment) newFragment;

            if (currentPage.isBlocked()) setNextPageSwipeLock(true);
            else setNextPageSwipeLock(false);

            if (currentPage.getOldCheckedRadio() == 0) {
                currentPage.getRadioGroup().check(R.id.no_radioButton);
            } else if (currentPage.getOldCheckedRadio() == 1) {
                currentPage.getRadioGroup().check(R.id.yes_radioButton);
            }
            Log.d("RESULT", Arrays.toString(answers));
        }
    }

    @Override
    public void onAnswer(boolean value, int layoutResId, int pageNumber) {
        Log.d(TAG, "onAnswer()");
        if (pageNumber != PAGE_RES) {
            answers[pageNumber] = value;
            return;
        }

        // End result
        if (value) {

        } else {

        }
    }

    /**
     * Open the cancellation message.
     */
    private void openCancelMessage() {
        runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.elderly_register_success));

            dialog.setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
            });

            dialog.setNegativeButton(R.string.no_text, (dialogInterface, which) -> {
            });

            dialog.setOnCancelListener((dialogInterface) -> {
            });

            dialog.create().show();
        });
    }

    /**
     *
     */
    private void processAssessment(int layoutResId) {

    }
}
