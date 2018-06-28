package br.edu.uepb.nutes.haniot.elderly;

import android.content.Intent;
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
        questions = getResources().getStringArray(R.array.risk_questions_array);
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
        // page 1
        addSlide(new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group1)
                .description(R.string.risk_fall_description_q1)
                .image(R.drawable.fall_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPink))
                .buttonClose(R.drawable.ic_action_close)
                .pageNumber(PAGE_1)
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
        else super.showMessageCancel();// Cancel result
    }

    @Override
    public void onClosePage() {
        super.showMessageCancel();
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

}

