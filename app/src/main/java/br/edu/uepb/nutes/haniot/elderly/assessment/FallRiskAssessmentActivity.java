package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

import java.util.Arrays;

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
    private final int PAGE_RES = 10;

    private boolean[] answers;
    private String[] questions;
    private boolean displayMessageBlock;
    private boolean isComingBackPage;


    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answers = new boolean[10];
        questions = getResources().getStringArray(R.array.risk_questions_array);
        displayMessageBlock = true;
        isComingBackPage = true;

        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(true);
        showSkipButton(true);
        setSkipText(getString(R.string.cancel_text));
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

        addSlide(SliderPageResultFragment.newInstance(
                R.layout.fragment_elderly_fall_risk_end,
                ContextCompat.getColor(this, R.color.colorDeepPurple),
                PAGE_RES));
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

    @Override
    public boolean onCanRequestNextPage() {
        return super.onCanRequestNextPage();
    }

    @Override
    public void onAnswer(View view, boolean value, int page) {
        if (page < PAGE_RES) {
            Log.d(TAG, "onAnswer() NOT END");
            answers[page] = value;
            return;
        }

        // End result
        if (value) {
            Log.d(TAG, "onAnswer() END - OK");
        } else {
            Log.d(TAG, "onAnswer() END - CANCEL");
        }
    }

    /**
     *
     */
    private void processAssessment(int layoutResId) {

    }

    private void showMessageBlocked() {
        if (!displayMessageBlock) return;

        final Snackbar snackbar = Snackbar.make(getCurrentFocus(),
                R.string.error_internal_device,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.bt_ok, (v) -> {
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
