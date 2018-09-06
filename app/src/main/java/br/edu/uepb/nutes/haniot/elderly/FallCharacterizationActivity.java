package br.edu.uepb.nutes.haniot.elderly;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseSurvey;
import br.edu.uepb.nutes.haniot.survey.pages.ButtonPage;
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
        SelectSpinnerPage.OnSpinnerListener, MultiSelectSpinnerPage.OnMultiSelectSpinnerListener, ButtonPage.OnButtonListener {

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
    private ButtonPage pageEnd;

    @Override
    public void initView() {
        questions = getResources().getStringArray(R.array.fall_risk_questions_array);
        answers = new boolean[10];

        Intent it = getIntent();
        elderlyId = it.getStringExtra(FallCharacterizationActivity.EXTRA_ELDERLY_ID);
        elderlyName = "Elvis Aaron Presley";
        elderlyFallDate = "15/07/2018 às 13:45:11";

        addPages();
    }

    /**
     * Add the slides.
     */
    private void addPages() {
        /**
         * Config pages.
         */
        showPagerIndicator(false);

        // Drawables
        int btCloseDrawable = R.drawable.ic_action_close_light;
        int radioLeftDrawable = R.drawable.button_background_blue_left;
        int radioRightDrawable = R.drawable.button_background_blue_right;

        // colors
        int colorText = ContextCompat.getColor(this, R.color.colorBlackGrey);
        int colorItems = ContextCompat.getColor(this, R.color.colorBlue);

        // page 1
        page1 = new RadioPage.ConfigPage()
                .title(getResources().getString(R.string.fall_characterization_q1, elderlyName, elderlyFallDate), colorText)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_1)
                .build();

        // page 2
        page2 = new RadioPage.ConfigPage()
                .title(R.string.fall_characterization_q2, colorText)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_2)
                .build();

        // page 3
        page3 = new RadioPage.ConfigPage()
                .title(R.string.fall_characterization_q3, colorText)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_3)
                .build();

        // page 4
        page4 = new SelectSpinnerPage.ConfigPage()
                .title(R.string.fall_characterization_q4, colorText)
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
                .title(R.string.fall_characterization_q5, colorText)
                .colorBackground(Color.WHITE)
                .radioStyle(radioLeftDrawable, radioRightDrawable, Color.BLACK, Color.WHITE)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_5)
                .build();

        // page 6
        page6 = new MultiSelectSpinnerPage.ConfigPage()
                .layout(R.layout.question_human_body)
                .title(R.string.fall_characterization_q6, colorText)
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
                .title(R.string.fall_characterization_q7, colorText)
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
                .title(R.string.fall_characterization_q8, colorText)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q8_array))))
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .buttonClose(btCloseDrawable)
                .pageNumber(PAGE_8)
                .build();

        // page 9
        page9 = new SelectSpinnerPage.ConfigPage()
                .title(R.string.fall_characterization_q9, colorText)
                .items(new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.answers_fall_characterization_q9_array))))
                .colorBackground(Color.WHITE)
                .colorBackgroundTint(colorItems)
                .colorSelectedText(colorItems)
                .buttonClose(btCloseDrawable)
                .disableAddNewItem()
                .pageNumber(PAGE_9)
                .build();

        pageEnd = new ButtonPage.ConfigPage()
                .description("Obrigado por concluir o formulário de caracterização da queda.")
                .buttonText(R.string.bt_ok)
                .buttonColorText(colorItems)
                .buttonBackground(R.drawable.button_background_blue)
                .colorBackground(Color.WHITE)
                .pageNumber(PAGE_END)
                .build();

        addSlide(page1);
        addSlide(page2);
        addSlide(page3);
        addSlide(page4);
        addSlide(page5);
        addSlide(page6);
        addSlide(page7);
        addSlide(page8);
        addSlide(page9);
        addSlide(pageEnd);
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

    @Override
    public void onClosePage() {
        showMessageCancel();
    }

    @Override
    public void onAnswerRadio(int page, boolean value) {
        Log.d(TAG, "onAnswerRadio() " + "PAGE: " + page + " | value: " + value);

        if (page == PAGE_1) {
            if (!value) {
                showMessageDidNotFall();
                return;
            }
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

    @Override
    public void onAnswerButton(int page) {
        if (page == PAGE_END) finish();
    }

    /**
     * Show dialog mesage cancel.
     */
    private void showMessageCancel() {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setMessage(R.string.fall_characterization_cancel_dialog)
                    .setPositiveButton(R.string.yes_text, (dialogInterface, which) -> finish())
                    .setNegativeButton(R.string.no_text, null)
                    .setNeutralButton(R.string.bt_remind_me_later, (dialogInterface, which) -> {
                        // TODO Criar service para lembrar
                        finish();
                    }).create().show();
        });
    }

    /**
     * Show dialog confirm did not fall.
     */
    private void showMessageDidNotFall() {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setTitle(R.string.warning_title)
                    .setIcon(R.drawable.ic_action_warning_light)
                    .setMessage(getResources().getString(R.string.fall_characterization_did_not_fall_dialog, elderlyName, elderlyFallDate))
                    .setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                        // TODO implementar o save fall como invalido/falso positivo
                        finish();
                    })
                    .setOnDismissListener(dialog -> currentPage.clearAnswer())
                    .setNegativeButton(R.string.no_text, null)
                    .create().show();
        });
    }

    /**
     * Process result assessment.
     */
    private void processAssessment() {

    }

}



