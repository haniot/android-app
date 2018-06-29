package br.edu.uepb.nutes.haniot.elderly;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseSurvey;
import br.edu.uepb.nutes.haniot.survey.pages.MultiSelectSpinnerPage;
import br.edu.uepb.nutes.haniot.survey.pages.RadioPage;
import br.edu.uepb.nutes.haniot.survey.pages.SpinnerPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FallCharacterizationActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallCharacterizationActivity extends BaseSurvey implements RadioPage.OnRadioListener,
        SpinnerPage.OnSpinnerListener, MultiSelectSpinnerPage.OnMultiSelectSpinnerListener {

    private final String TAG = "FallCharacterization";

    public static final String EXTRA_QUESTIONS = "extra_questions";
    public static final String EXTRA_ANSWERS = "extra_answers";
    public static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

    private final int PAGE_1 = 1;
    private final int PAGE_2 = 2;
    private final int PAGE_3 = 3;
    private final int PAGE_4 = 4;
    private final int PAGE_5 = 5;
    private final int PAGE_6 = 6;

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
                .title(R.string.risk_fall_title_group1, ContextCompat.getColor(this, R.color.colorBlackGrey))
                .description(R.string.risk_fall_description_q1,
                        ContextCompat.getColor(this, R.color.colorBlackGrey))
                .backgroundColor(Color.WHITE)
                .radioStyle(R.drawable.button_background_blue_left, R.drawable.button_background_blue_right, Color.BLACK, Color.WHITE)
                .buttonClose(R.drawable.ic_action_close_light)
                .pageNumber(PAGE_1)
                .build();

        // page 2
        SpinnerPage page2 = new SpinnerPage.ConfigPage()
                .title(R.string.risk_fall_description_q9, ContextCompat.getColor(this, R.color.colorBlackGrey))
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .backgroundColor(Color.WHITE)
                .colorTextItemSelected(ContextCompat.getColor(this, R.color.colorBlue))
                .buttonClose(R.drawable.ic_action_close_light)
                .pageNumber(PAGE_2)
                .build();

        // page 3
        SpinnerPage page3 = new SpinnerPage.ConfigPage()
                .title(R.string.risk_fall_description_q3)
                .colorTextItemSelected(Color.WHITE)
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .buttonClose(R.drawable.ic_action_close_light)
                .pageNumber(PAGE_3)
                .build();

        // page 4
        RadioPage page4 = new RadioPage.ConfigPage()
                .title(R.string.risk_fall_title_group4)
                .description(R.string.risk_fall_description_q5)
                .image(R.drawable.coast_pain_elderly)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorLightGreen))
                .buttonClose(R.drawable.ic_action_close_light)
                .pageNumber(PAGE_4)
                .build();

        MultiSelectSpinnerPage page5 = new MultiSelectSpinnerPage.ConfigPage()
                .description(R.string.risk_fall_description_q6, ContextCompat.getColor(this, R.color.colorBlackGrey))
                .items(new ArrayList(Arrays.asList(new String[]{"Estacada", "Banheiro", "Cozinha", "Sala de Estar", "Varanda"})))
                .backgroundColor(Color.WHITE)
                .colorTextItemSelected(ContextCompat.getColor(this, R.color.colorBlue))
                .buttonClose(R.drawable.ic_action_close_light)
                .pageNumber(PAGE_5)
                .build();

        MultiSelectSpinnerPage page6 = new MultiSelectSpinnerPage.ConfigPage()
                .layout(R.layout.question_human_body)
                .title(R.string.risk_fall_description_q1, ContextCompat.getColor(this, R.color.colorBlackGrey))
                .items(new ArrayList(Arrays.asList(getResources().getStringArray(R.array.human_body_array))))
                .image(R.drawable.human_body)
                .backgroundColor(Color.WHITE)
                .colorTextItemSelected(ContextCompat.getColor(this, R.color.colorBlue))
                .buttonClose(R.drawable.ic_action_close_light)
                .disableAddNewItem()
                .pageNumber(PAGE_6)
                .build();

        addSlide(page6);
        addSlide(page5);
        addSlide(page1);
        addSlide(page2);
        addSlide(page3);
        addSlide(page4);

        // page end
        addSlide(new RadioPage.ConfigPage()
                .layout(R.layout.fragment_elderly_fall_risk_end)
                .pageNumber(PAGE_END)
                .build());
    }

    @Override
    public void onClosePage() {
        showMessageCancel();
    }

    @Override
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() " + "PAGE: " + page + " | value: " + value);
        if (page != PAGE_END) {
            currentPage.nextPage();
        }
    }

    @Override
    public void onAnswerSpinner(int page, String value, int indexValue) {
        Log.d(TAG, "onAnswerSpinner() " + "PAGE: " + page + " | value: " + value + " index: " + indexValue);
        if (page != PAGE_END) {
            currentPage.nextPage();
        }
    }

    @Override
    public void onMultiSelectSpinner(int page, List<String> values, List<Integer> indexValues) {
        Log.d(TAG, "onMultiSelectSpinner() " + "PAGE: " + page + " | values: "
                + Arrays.toString(values.toArray()) + " indexs: " + Arrays.toString(indexValues.toArray()));
        if (page != PAGE_END) {
            currentPage.nextPage();
        }
    }

    /**
     * Process result assessment.
     */
    private void processAssessment() {

    }

}



