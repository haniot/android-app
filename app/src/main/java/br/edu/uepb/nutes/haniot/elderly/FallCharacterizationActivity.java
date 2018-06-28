package br.edu.uepb.nutes.haniot.elderly;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.IBasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnSwipePageTouchListener;
import br.edu.uepb.nutes.haniot.survey.pages.MultiSelectSpinnerPage;
import br.edu.uepb.nutes.haniot.survey.pages.RadioPage;
import br.edu.uepb.nutes.haniot.survey.pages.SpinnerPage;

/**
 * FallCharacterizationActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallCharacterizationActivity extends AppIntro implements RadioPage.OnRadioListener,
        SpinnerPage.OnSpinnerListener, MultiSelectSpinnerPage.OnMultiSelectSpinnerListener {

    private final String TAG = "FallRiskAssActivity";

    public static final String EXTRA_QUESTIONS = "extra_questions";
    public static final String EXTRA_ANSWERS = "extra_answers";
    public static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

    private final int PAGE_1 = 1;
    private final int PAGE_2 = 2;
    private final int PAGE_3 = 3;
    private final int PAGE_4 = 4;
    private final int PAGE_5 = 5;
    private final int PAGE_END = 6;

    private String[] questions;
    private boolean[] answers;
    private IBasePage currentPage;
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
                .title(R.string.risk_fall_title_group1)
                .description(R.string.risk_fall_description_q1)
                .image(R.drawable.fall_elderly)
                .radioRightText(R.string.action_bond)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_1)
                .answerInit(false)
                .build();

        // page 2
        SpinnerPage page2 = new SpinnerPage.ConfigPage()
                .layout(R.layout.question_spinner_theme_dark)
                .title(R.string.risk_fall_description_q9, Color.WHITE)
                .colorTextItemSelected(Color.WHITE)
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .pageNumber(PAGE_2)
                .build();

        // page 3
        SpinnerPage page3 = new SpinnerPage.ConfigPage()
                .layout(R.layout.question_spinner_theme_light)
                .title(R.string.risk_fall_description_q3)
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .pageNumber(PAGE_3)
                .build();

        // page 4
        RadioPage page4 = new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group4)
                .description(R.string.risk_fall_description_q5)
                .image(R.drawable.coast_pain_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorLightGreen))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_4)
                .build();

        MultiSelectSpinnerPage page5 = new MultiSelectSpinnerPage.ConfigPage()
                .title(R.string.risk_fall_title_group6)
                .description(R.string.risk_fall_description_q6)
                .image(R.drawable.fall_elderly)
                .buttonClose(R.drawable.ic_action_close)
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .colorTextItemSelected(Color.WHITE)
                .hint(R.string.required_field)
                .pageNumber(PAGE_5)
                .build();


        addSlide(page1);
        addSlide(page2);
        addSlide(page3);
        addSlide(page4);
        addSlide(page5);

//        page1.setAnswer(true);

        // page end
        addSlide(new RadioPage.ConfigPage()
                .layout(R.layout.fragment_elderly_fall_risk_end)
                .pageNumber(PAGE_END)
                .build());
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        Log.d(TAG, (oldFragment != null ? "  oldFragment() " + oldFragment.getClass().getName() : "")
                + " | " + (newFragment != null ? "  newFragment() " + newFragment.getClass().getName() : ""));

        if (snackbarMessageBlockedPage != null)
            snackbarMessageBlockedPage.dismiss();

        if (newFragment instanceof IBasePage) {
            currentPage = (IBasePage) newFragment;

            if (currentPage.getPageNumber() == PAGE_END) return;

            if (currentPage.isBlocked()) setNextPageSwipeLock(true);
            else setNextPageSwipeLock(false);

            // Capture event onSwipeLeft
            currentPage.getView().setOnTouchListener(new OnSwipePageTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if (currentPage.isBlocked()) showMessageBlocked();
                }
            });
        }
    }

    private void setAnswerInit() {
        if (currentPage == null) return;

        Log.d("setAnswerInit", "page " + currentPage.getPageNumber());


        switch (currentPage.getPageNumber()) {
//            case PAGE_1:
//                if(currentPage instanceof RadioPage)
//                    ((RadioPage) currentPage).setAnswer(false);
//                break;
//            case PAGE_3:
//                if(currentPage instanceof SpinnerPage)
//                    ((SpinnerPage) currentPage).setAnswer(3);
//            case PAGE_5:
//                if(currentPage instanceof MultiSelectSpinnerPage)
//                    ((MultiSelectSpinnerPage) currentPage).setAnswer(Arrays.asList(0, 3));
            default:
                break;
        }
    }

    /**
     * Process result assessment.
     */
    private void processAssessment() {
        Intent intent = new Intent(this, FallRiskResultActivity.class);
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
                if (currentPage.getPageNumber() == PAGE_END)
                    currentPage.clearAnswer();
            });

            dialog.setOnCancelListener((dialogInterface) -> {
                if (currentPage.getPageNumber() == PAGE_END)
                    currentPage.clearAnswer();
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
    public void onClosePage() {
        showMessageCancel();
    }

    @Override
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() " + "PAGE: " + page + " | value: " + value);
        if (page != PAGE_END) {
//            currentPage.nextPage();
        }
    }

    @Override
    public void onAnswerSpinner(int page, String value, int indexValue) {
        Log.d(TAG, "onAnswerSpinner() " + "PAGE: " + page + " | value: " + value + " index: " + indexValue);
        if (page != PAGE_END) {
//            currentPage.nextPage();
        }
    }

    @Override
    public void onMultiSelectSpinner(int page, List<String> values, List<Integer> indexValues) {
        Log.d(TAG, "onMultiSelectSpinner() " + "PAGE: " + page + " | values: "
                + Arrays.toString(values.toArray()) + " indexs: " + Arrays.toString(indexValues.toArray()));
        if (page != PAGE_END) {
//            currentPage.nextPage();
        }
    }
}



