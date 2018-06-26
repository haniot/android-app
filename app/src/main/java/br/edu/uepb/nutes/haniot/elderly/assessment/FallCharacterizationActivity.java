package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import br.edu.uepb.nutes.haniot.elderly.assessment.pages.*;

import com.github.paolorotolo.appintro.AppIntro;

import java.util.ArrayList;
import java.util.Arrays;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.elderly.ElderlyRegisterActivity;

/**
 * FallCharacterizationActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallCharacterizationActivity extends AppIntro implements RadioPage.OnAnswerRadioListener,
        SpinnerPage.OnAnswerSpinnerListener {

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
    private BasePage currentPage;
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
        showSeparator(false);
        showSkipButton(false);
        showPagerIndicator(false);
        setNextPageSwipeLock(true);
        setImmersive(true);

        // page 1
        RadioPage page1 = new RadioPage.ConfigPage()
                .title(R.string.risk_fall_description_q1)
                .description(R.string.risk_fall_description_q5)
                .image(R.drawable.elderly_happy)
                .buttonClose(R.drawable.ic_action_close)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPurple))
                .pageNumber(PAGE_1)
                .build();

        // page 2
        addSlide(page1);


        addSlide(new SpinnerPage.ConfigPage()
                .title(R.string.title_save_captured_data)
                .description(R.string.risk_fall_description_q2)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorBlackGrey))
                .hint(R.string.elderly_select_accessories)
                .items(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.elderly_accessories_array))))
                .colorTextItemSelected(ContextCompat.getColor(this, R.color.colorTextDark))
                .pageNumber(PAGE_2)
                .build());

        addSlide(new SpinnerPage.ConfigPage()
                .layout(R.layout.question_spinner_theme_light)
                .title(R.string.risk_fall_description_q9)
                .hint(R.string.elderly_select_medications)
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .colorTextItemSelected(ContextCompat.getColor(this, R.color.colorPurple))
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

        if (newFragment instanceof BasePage) {
            currentPage = (BasePage) newFragment;
            Log.d(TAG, "onSlideChanged() " + currentPage.getClass().getName());

            if (currentPage.getPageNumber() == PAGE_END) return;

            if (currentPage.isBlocked()) setNextPageSwipeLock(true);
            else setNextPageSwipeLock(false);

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
            });

            dialog.setOnCancelListener((dialogInterface) -> {
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

    @Override
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() " + "PAGE: " + page + " | value: " + value);
    }

    @Override
    public void onClosePage() {
        Log.d(TAG, "onClosePage");
    }


    @Override
    public void onAnswerSpinner(int page, String value, int indexValue) {
        Log.d(TAG, "onAnswerSpinner() " + "PAGE: " + page + " | value: " + value + " index: " + indexValue);
    }
}



