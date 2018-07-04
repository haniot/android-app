package br.edu.uepb.nutes.haniot.elderly;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseSurvey;
import br.edu.uepb.nutes.haniot.survey.pages.RadioPage;
import br.edu.uepb.nutes.haniot.utils.Log;

/**
 * FallRiskActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallRiskActivity extends BaseSurvey implements RadioPage.OnRadioListener {
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

    private String[] questions;
    private boolean[] answers;

    private String elderlyId;

    @Override
    public void initView() {
        questions = getResources().getStringArray(R.array.fall_risk_questions_array);
        answers = new boolean[10];

        Intent it = getIntent();
        elderlyId = it.getStringExtra(ElderlyRegisterActivity.EXTRA_ELDERLY_ID);

        addPages();
    }

    /**
     * Add the slides.
     * <p>
     * AGES THEME DARK /layout/question_radio_theme_dark.xml
     */
    private void addPages() {
        // Drawables
        int radioLeftDrawable = R.drawable.button_background_white_left;
        int radioRightDrawable = R.drawable.button_background_white_right;

        // colors
        int colorText = ContextCompat.getColor(this, R.color.colorTextDark);

        // page 1
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group1, colorText)
                .description(R.string.fall_risk_description_q1, colorText)
                .image(R.drawable.fall_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorPink))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_1)
                .build());

        // page 2
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group2, colorText)
                .description(R.string.fall_risk_description_q2, colorText)
                .image(R.drawable.walker_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorPurple))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_2)
                .build());

        // page 3
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group3, colorText)
                .description(R.string.fall_risk_description_q3, colorText)
                .image(R.drawable.medications_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorLightBlue))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_3)
                .build());

        // page 4
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group3, colorText)
                .description(R.string.fall_risk_description_q4, colorText)
                .image(R.drawable.medications_2_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorLightBlue))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_4)
                .build());

        // page 5
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group4, colorText)
                .description(R.string.fall_risk_description_q5, colorText)
                .image(R.drawable.coast_pain_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorLightGreen))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_5)
                .build());

        // page 6
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group5, colorText)
                .description(R.string.fall_risk_description_q6, colorText)
                .image(R.drawable.daily_activity_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_6)
                .build());

        // page 7
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group6, colorText)
                .description(R.string.fall_risk_description_q7, colorText)
                .image(R.drawable.difficulty_seeing_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_7)
                .build());

        // page 8
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group6, colorText)
                .description(R.string.fall_risk_description_q8, colorText)
                .image(R.drawable.hearing_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_8)
                .build());

        // page 9
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group7, colorText)
                .description(R.string.fall_risk_description_q9, colorText)
                .image(R.drawable.physical_activity_elderly)
                .colorBackground(ContextCompat.getColor(this, R.color.colorIndigo))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_9)
                .build());

        // page 10
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.fall_risk_title_group8, colorText)
                .description(R.string.fall_risk_description_q10, colorText)
                .image(R.drawable.abajur)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .radioStyle(radioLeftDrawable, radioRightDrawable, colorText, Color.BLACK)
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_10)
                .build());

        // page end
        addSlide(new RadioPage.ConfigPage()
                .layout(R.layout.fragment_elderly_fall_risk_end)
                .pageNumber(super.PAGE_END)
                .build());
    }

    @Override
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() | value: " + value + " page: " + page);

        if (page != super.PAGE_END) {
            answers[page] = value;
            currentPage.nextPage();
            return;
        }

        if (value) this.processAssessment();// End result
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
        Intent intent = new Intent(this, FallRiskResultActivity.class);
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
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setMessage(getString(R.string.fall_risk_title_cancel))
                    .setPositiveButton(R.string.yes_text, (dialogInterface, which) -> finish())
                    .setNegativeButton(R.string.no_text, null)
                    .setOnDismissListener(dialogInterface -> {
                        if (currentPage.getPageNumber() == PAGE_END)
                            currentPage.clearAnswer();
                    }).create().show();
        });
    }


}

