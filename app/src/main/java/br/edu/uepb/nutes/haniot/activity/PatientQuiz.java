package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.simplesurvey.base.SimpleSurvey;
import br.edu.uepb.nutes.simplesurvey.question.DichotomicChoice;
import br.edu.uepb.nutes.simplesurvey.question.Infor;
import br.edu.uepb.nutes.simplesurvey.question.MultipleChoice;
import br.edu.uepb.nutes.simplesurvey.question.Open;
import br.edu.uepb.nutes.simplesurvey.question.SingleChoice;


public class PatientQuiz extends SimpleSurvey implements Infor.OnInfoListener,
        DichotomicChoice.OnDichotomicListener, SingleChoice.OnSingleListener,
        MultipleChoice.OnMultipleListener,
        Open.OnTextBoxListener {

    private final String LOG_TAG = PatientQuiz.class.getSimpleName();
    private final int FIRST_PAGE = 0;
    private final int END_PAGE = -1;
    private final int CATEGORY_PAGE = -2;

    @Override
    protected void initView() {
        addPages();
    }

    private void addPages() {
        setMessageBlocked(getResources().getString(R.string.not_answered));
        /**
         * Available animations:
         *    - setFadeAnimation()
         *    - setZoomAnimation()
         *    - setFlowAnimation()
         *    - setSlideOverAnimation()
         *    - setDepthAnimation()
         * More details: {https://github.com/AppIntro/AppIntro#animations}
         */
        setFadeAnimation();

        //INTRO PAGE
        addQuestion(new Infor.Config()
                .layout(R.layout.welcome)
                .colorBackground(getResources().getColor(R.color.colorPrimaryDark))
                .nextQuestionAuto()
                .pageNumber(FIRST_PAGE)
                .build());

        //CATEGORY 2
        addQuestion(new Infor.Config()
                .title(R.string.category_2, Color.WHITE)
                .description(R.string.category_2_desc,
                        Color.WHITE)
                .image(R.drawable.x_sneaker)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_PAGE)
                .build());


        addQuestion(new MultipleChoice.Config()
                .title(getString(R.string.q1), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .description("")
                .image(R.drawable.x_trophy)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputItems(parseAnswers(R.array.sports_answers))
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .nextQuestionAuto()
                .inputDisableAddNewItem()
                .pageNumber(4)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q2), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_gymnastics)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_sports_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        //CATEGORY 3
        addQuestion(new Infor.Config()
                .title(R.string.category_3, Color.WHITE)
                .description(R.string.category_3_desc,
                        Color.WHITE)
                .image(R.drawable.x_diet)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_PAGE)
                .build());


        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q3), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .image(R.drawable.x_breakfast)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_lunch_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(6)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q4), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .image(R.drawable.x_water)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_water_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(7)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q5), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorYellow))
                .image(R.drawable.x_beef)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_beef_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(8)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_soda_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(9)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q7), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .image(R.drawable.x_salad)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_salad_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(10)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q8), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .image(R.drawable.x_fries)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_freats_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(11)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q9), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_milk)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_milk_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(12)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q10), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorAmber))
                .image(R.drawable.x_beans)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_beans_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(13)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q11), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .image(R.drawable.x_apple)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_fruits_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(14)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q12), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .image(R.drawable.x_lollipop)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_goodies_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(15)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q13), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .image(R.drawable.x_sausages)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_sausages_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(16)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q14), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorPink))
                .image(R.drawable.x_breastfeeding)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.breast_feeding_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(17)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q15), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .image(R.drawable.x_allergy)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.allergy_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(18)
                .build());

        //CATEGORY 4
        addQuestion(new Infor.Config()
                .title(R.string.category_4, Color.WHITE)
                .description(R.string.category_4_desc,
                        Color.WHITE)
                .image(R.drawable.x_drug)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_PAGE)
                .build());


        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q16), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_blood_pressure)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_or_no_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(19)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q17), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .image(R.drawable.x_glucosemeter)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_or_no_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(20)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q18), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .image(R.drawable.x_blood)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_or_no_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(21)
                .build());

        //CATEGORY 5
        addQuestion(new Infor.Config()
                .title(R.string.category_5, Color.WHITE)
                .description(R.string.category_5_desc,
                        Color.WHITE)
                .image(R.drawable.x_bed)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_PAGE)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q19), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .image(R.drawable.x_sleep)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.hours_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(22)
                .build());

        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q20), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .image(R.drawable.x_wakeup)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.hours_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(23)
                .build());

        //END PAGE
        addQuestion(new Infor.Config()
                .title(R.string.thank_you, Color.WHITE)
                .description(R.string.final_instructions)
                .colorBackground(getResources().getColor(R.color.colorPrimaryDark))
                .image(R.drawable.x_like)
                .buttonClose(R.drawable.ic_action_close_dark)
                .buttonColorText(getResources().getColor(R.color.colorPrimaryDark))
                .buttonBackground(R.drawable.button_stylezed)
                .pageNumber(END_PAGE)
                .build());
    }

    public List<String> parseAnswers(int id) {
        ArrayList<String> answers = new ArrayList<>();
        String[] parseAnswers = getResources().getStringArray(id);
        Collections.addAll(answers, parseAnswers);
        return answers;
    }

    @Override
    public void onClosePage() {
        new AlertDialog
                .Builder(this)
                .setMessage(getResources().getString(R.string.cancel))
                .setPositiveButton(getResources().getText(R.string.yes_text), (dialog, which) -> finish())
                .setNegativeButton(getResources().getText(R.string.no_text), null)
                .show();
    }

    @Override
    public void onAnswerInfo(int page) {
        Log.d(LOG_TAG, "onAnswerInfo() | PAGE: " + page);
        if (page == END_PAGE) { //
            finish();
        }
    }

    @Override
    public void onAnswerDichotomic(int page, boolean value) {
        Log.d(LOG_TAG, "onAnswerDichotomic() | PAGE:  " + page + " | ANSWER: " + value);
    }

    @Override
    public void onAnswerSingle(int page, String value, int indexValue) {
        Log.d(LOG_TAG, "onAnswerMultiple() | PAGE:  " + page
                + " | ANSWER (value): " + value
                + " | ANSWER (index): " + indexValue);

        switch (page) {
            case 5:

                break;
            case 6:

                break;
            case 7:

                break;
            case 8:

                break;
            case 9:

                break;
            case 10:

                break;
            case 11:

                break;
            case 12:

                break;
            case 13:

                break;
            case 14:

                break;
            case 15:

                break;
            case 16:

                break;
            case 17:

                break;
            case 18:

                break;
            case 19:

                break;
            case 20:

                break;
            case 21:

                break;
            case 22:

                break;
            case 23:

                break;
        }
    }

    @Override
    public void onAnswerMultiple(int page, List<String> values, List<Integer> indexValues) {
        Log.d(LOG_TAG, "onAnswerMultiple() | PAGE:  " + page
                + " | ANSWER (values): " + Arrays.toString(values.toArray())
                + " | ANSWER (indexes): " + Arrays.toString(indexValues.toArray()));

        switch (page) {
            case 4:

                break;
        }
    }

    @Override
    public void onAnswerTextBox(int page, String value) {
        Log.d(LOG_TAG, "onAnswerTextBox() | PAGE:  " + page
                + " | ANSWER: " + value);
    }
}
