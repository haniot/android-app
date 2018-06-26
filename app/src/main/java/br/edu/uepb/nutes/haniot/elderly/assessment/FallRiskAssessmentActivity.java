package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.elderly.ElderlyRegisterActivity;
import br.edu.uepb.nutes.haniot.elderly.assessment.pages.OnSwipeTouchListener;
import br.edu.uepb.nutes.haniot.elderly.assessment.pages.RadioPage;
import br.edu.uepb.nutes.haniot.utils.Log;

/**
 * FallRiskAssessmentActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallRiskAssessmentActivity extends AppIntro implements RadioPage.OnAnswerRadioListener {
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
    private RadioPage currentPage;
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
         * Config pages.
         */
        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(true);
        showSkipButton(false);
        setNextPageSwipeLock(true);
        setImmersive(true);

        // PAGES THEME DARK /layout/question_radio_theme_dark.xml

        // page 1
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group1)
                .description(R.string.risk_fall_description_q1)
                .image(R.drawable.fall_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPink))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_2)
                .build());

        // page 2
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group2)
                .description(R.string.risk_fall_description_q2)
                .image(R.drawable.walker_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPurple))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_2)
                .build());

        // page 3
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group3)
                .description(R.string.risk_fall_description_q3)
                .image(R.drawable.medications_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorLightBlue))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_3)
                .build());

        // page 4
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group3)
                .description(R.string.risk_fall_description_q4)
                .image(R.drawable.medications_2_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorLightBlue))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_4)
                .build());

        // page 5
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group4)
                .description(R.string.risk_fall_description_q5)
                .image(R.drawable.coast_pain_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorLightGreen))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_5)
                .build());

        // page 6
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group5)
                .description(R.string.risk_fall_description_q6)
                .image(R.drawable.daily_activity_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorOrange))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_6)
                .build());

        // page 7
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group6)
                .description(R.string.risk_fall_description_q7)
                .image(R.drawable.difficulty_seeing_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_7)
                .build());

        // page 8
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group6)
                .description(R.string.risk_fall_description_q8)
                .image(R.drawable.hearing_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_8)
                .build());

        // page 9
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group7)
                .description(R.string.risk_fall_description_q9)
                .image(R.drawable.physical_activity_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorIndigo))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_9)
                .build());

        // page 10
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group8)
                .description(R.string.risk_fall_description_q10)
                .image(R.drawable.abajur)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorCyan))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_10)
                .build());

        // page end
        addSlide(new RadioPage.ConfigPage()
                .layout(R.layout.fragment_elderly_fall_risk_end)
                .pageNumber(PAGE_END)
                .build());
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

        if (newFragment instanceof RadioPage) {
            currentPage = (RadioPage) newFragment;

            if (currentPage.getPageNumber() == PAGE_END) return;

            if (currentPage.isBlocked()) setNextPageSwipeLock(true);
            else setNextPageSwipeLock(false);

            // Selects the last response of the current page
            if (currentPage.getOldAnswer() != -1)
                currentPage.setAnswer(currentPage.getOldAnswer() != 0);

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
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() | value: " + value + " page: " + page) ;

        if (page < PAGE_END) {
            answers[page] = value;
            return;
        }

        if (value) processAssessment();// End result
        else showMessageCancel();// Cancel result
    }

    @Override
    public void onClosePage() {
        showMessageCancel();
    }

    /**
     * Process result assessment.
     */
    private void processAssessment() {
        Intent intent = new Intent(this, FallRiskAssessmentResultActivity.class);
        intent.putExtra(EXTRA_ANSWERS, answers);
        intent.putExtra(EXTRA_QUESTIONS, questions);
        intent.putExtra(EXTRA_ELDERLY_ID, elderlyId);
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
}

