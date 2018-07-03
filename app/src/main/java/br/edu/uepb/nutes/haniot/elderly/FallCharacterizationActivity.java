package br.edu.uepb.nutes.haniot.elderly;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.PagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseSurvey;
import br.edu.uepb.nutes.haniot.survey.pages.MultiSelectSpinnerPage;
import br.edu.uepb.nutes.haniot.survey.pages.RadioPage;
import br.edu.uepb.nutes.haniot.survey.pages.SelectSpinnerPage;

/**
 * FallCharacterizationActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallCharacterizationActivity extends BaseSurvey implements RadioPage.OnRadioListener,
        SelectSpinnerPage.OnSpinnerListener, MultiSelectSpinnerPage.OnMultiSelectSpinnerListener {

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
    private final int PAGE_7 = 7;
    private final int PAGE_8 = 8;
    private final int PAGE_9 = 9;

    private String[] questions;
    private boolean[] answers;
    private String elderlyId;
    private String elderlyName;
    private String elderlyFallDate;

    // pages
    private RadioPage page1, page2, page3, page5;
    private SelectSpinnerPage page4, page8, page9;
    private MultiSelectSpinnerPage page6, page7;

    @Override
    public void initView() {
        questions = getResources().getStringArray(R.array.fall_risk_questions_array);
        answers = new boolean[10];

        Intent it = getIntent();
        elderlyId = it.getStringExtra(ElderlyRegisterActivity.EXTRA_ELDERLY_ID);
        elderlyName = "Elvis da Silva Santos";
        elderlyFallDate = "30/06/2018 as 13:45:11";

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

        // Drawables
        int btCloseDrawable = R.drawable.ic_action_close_light;
        int radioLeftDrawable = R.drawable.button_background_blue_left;
        int radioRightDrawable = R.drawable.button_background_blue_right;

        // colors
        int colorTitle = ContextCompat.getColor(this, R.color.colorBlackGrey);
        int colorItems = ContextCompat.getColor(this, R.color.colorBlue);

        // page 1
        page1 = new RadioPage.ConfigPage()
                .title(getResources().getString(R.string.fall_characterization_q1, elderlyName, elderlyFallDate), colorTitle)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_1)
                .build();

        // page 2
        page2 = new RadioPage.ConfigPage()
                .title(R.string.fall_characterization_q2, colorTitle)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_2)
                .build();

        // page 3
        page3 = new RadioPage.ConfigPage()
                .title(R.string.fall_characterization_q3, colorTitle)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_3)
                .build();

        // page 4
        page4 = new SelectSpinnerPage.ConfigPage()
                .title(R.string.fall_characterization_q4, colorTitle)
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q4_array))))
                .buttonClose(btCloseDrawable)
                .disableAddNewItem()
                .pageNumber(PAGE_4)
                .build();

        // page 5
        page5 = new RadioPage.ConfigPage()
                .title(R.string.fall_characterization_q5, colorTitle)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_5)
                .build();

        // page 6
        page6 = new MultiSelectSpinnerPage.ConfigPage()
                .layout(R.layout.question_human_body)
                .title(R.string.fall_characterization_q6, colorTitle)
                .image(R.drawable.human_body)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q6_array))))
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .buttonClose(btCloseDrawable)
                .enableZoomImage()
                .disableAddNewItem()
                .pageNumber(PAGE_6)
                .build();

        // page 7
        page7 = new MultiSelectSpinnerPage.ConfigPage()
                .title(R.string.fall_characterization_q7, colorTitle)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q7_array))))
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .disableAddNewItem()
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_7)
                .build();

        // page 8
        page8 = new SelectSpinnerPage.ConfigPage()
                .title(R.string.fall_characterization_q8, colorTitle)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q8_array))))
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .pageNumber(PAGE_8)
                .build();

        // page 9
        page9 = new SelectSpinnerPage.ConfigPage()
                .title(R.string.fall_characterization_q9, colorTitle)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q9_array))))
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .buttonClose(btCloseDrawable)
                .disableAddNewItem()
                .pageNumber(PAGE_9)
                .build();

        addSlide(page1);
        addSlide(page2);
    }

    private void removeAllPages(int offset) {
        Log.d(TAG, "removeAllPages() offset: " + offset);
        List<Fragment> _tempPages = new ArrayList(super.fragments);

        for (int i = offset; i < _tempPages.size(); i++) {
            super.fragments.remove(_tempPages.get(i));
            Log.d(TAG, "removeAllPages() TOTAL: " + super.fragments.size());
        }
        super.mPagerAdapter.notifyDataSetChanged();
    }

    private void removePagesGroup1() {
        Log.d(TAG, "removePagesGroup1() init total: " + fragments.size());
        super.fragments.remove(page3);
        super.fragments.remove(page4);
        super.fragments.remove(page5);
        super.fragments.remove(page6);
        super.fragments.remove(page7);
        super.fragments.remove(page8);
        super.fragments.remove(page9);

//        super.fragments.clear();

        PagerAdapter adpter = mPagerAdapter;
//        super.pager.setAdapter(null);
//        super.pager.setAdapter(super.mPagerAdapter);

        super.mPagerAdapter = null;
        super.mPagerAdapter = adpter;

//        super.mPagerAdapter.notifyDataSetChanged();
        Log.d(TAG, "removePagesGroup1() end total: " + fragments.size());
    }

    @Override
    public void onClosePage() {
        showMessageCancel();
    }

    @Override
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() " + "PAGE: " + page + " | value: " + value);

        /**
         * Attention!
         * Pages are added according to the YES / NO answers.
         *
         * 1 - If the answer on page 1 is YES, go to page 2, if NO, quit the flow.
         * 2 - If the answer on page 2 is YES, go to page 3, if NO, go to page 5.
         * 3 - If the answer on page 3 is YES, go to page 4, if NO, go to page 5.
         * 4 - If the answer on page 5 is YES, go to page 6, if NO, go to page 7.
         * 5 - After that adds to the remaining pages.
         */

        if (page == PAGE_1) {
            if (!value) finish(); // TODO EXIBIR MEENSAGEM DE FINILIZAÇÂO
        } else if (page == PAGE_2) {
//            removePagesGroup1();

            if (value) addSlide(page3);
            else addSlide(page5);
        } else if (page == PAGE_3) {
            removeAllPages(PAGE_3);

            if (value) addSlide(page4);
            else addSlide(page5);
        } else if (page == PAGE_5) {
            removeAllPages(PAGE_5);
            if (value) addSlide(page6);
            else addSlide(page7);

            // other pages
            addSlide(page8);
            addSlide(page9);

            // TODO adicionar pagina final!!!
        }

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



