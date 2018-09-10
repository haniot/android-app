package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.elderly.ElderlyRegisterActivity;

/**
 * FallRiskAssessmentActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class FallRiskAssessmentActivity extends AppIntro implements OnAnswerListener {
    private final String TAG = "FallRiskAssActivity";

    public static final String EXTRA_QUESTIONS = "extra_questions";
    public static final String EXTRA_ANSWERS = "extra_answers";
    public static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

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
    private Snackbar snackbarMessageBlockedPage;
    private String elderlyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        questions = getResources().getStringArray(R.array.risk_questions_array);
        answers = new boolean[10];

        Intent it = getIntent();
        elderlyId = it.getStringExtra(ElderlyRegisterActivity.EXTRA_ELDERLY_ID);

        initComponents();
    }

    /**
     * Initialize components.
     */
    private void initComponents() {
        addPages();
    }

    /**
     * Add the slides.
     */
    private void addPages() {
        /**
         * Builder pages.
         */
        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(true);
        showSkipButton(false);
        setNextPageSwipeLock(true);
        setImmersive(true);

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
                R.drawable.medications_2_elderly,
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
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        if (snackbarMessageBlockedPage != null)
            snackbarMessageBlockedPage.dismiss();

        if (newFragment instanceof SliderPageFragment) {
            currentPage = (SliderPageFragment) newFragment;

            if (currentPage.getPageNumber() == PAGE_END) return;

            if (currentPage.isBlocked()) setNextPageSwipeLock(true);
            else setNextPageSwipeLock(false);

            if (currentPage.getOldCheckedRadio() == 0)
                currentPage.getRadioGroup().check(R.id.no_radioButton);
            else if (currentPage.getOldCheckedRadio() == 1)
                currentPage.getRadioGroup().check(R.id.yes_radioButton);

            // Capture event onSwipeLeft
            currentPage.getView().setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if (currentPage.isBlocked()) showMessageBlocked();
                }
            });
        }
    }

    @Override
    public void onAnswer(View view, boolean value, int page) {
        if (page < PAGE_END) {
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
        intent.putExtra(EXTRA_ANSWERS, answers);
        intent.putExtra(EXTRA_QUESTIONS, questions);
        intent.putExtra(EXTRA_ELDERLY_ID, elderlyId);
        Log.d("TEST", "ij " + elderlyId);
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
                currentPage.clearCheck();
            });

            dialog.setOnCancelListener((dialogInterface) -> {
                currentPage.clearCheck();
            });

            dialog.create().show();
        });
    }

    /**
     * Show message page blocked.
     */
    private void showMessageBlocked() {
        runOnUiThread(() -> {
            /**
             * Create snackbar
             */
            snackbarMessageBlockedPage = Snackbar.make(currentPage.getView(),
                    R.string.risk_fall_message_blocked_page,
                    Snackbar.LENGTH_LONG);
            snackbarMessageBlockedPage.setAction(R.string.bt_ok, (v) -> {
                snackbarMessageBlockedPage.dismiss();
            });
            snackbarMessageBlockedPage.show();
        });
    }

    /**
     * Implementation OnSwipeTouchListener.
     */
    public class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        public void onSwipeLeft() {
        }

        public void onSwipeRight() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0) onSwipeRight();
                    else onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }
}

