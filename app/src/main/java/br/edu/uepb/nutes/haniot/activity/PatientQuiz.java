package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.InputType;

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

    @Override
    protected void initView() {
        addPages();
    }

    private void addPages() {
        setMessageBlocked("Oops! answer so i can go to the next question...");
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

        addQuestion(new Infor.Config()
                .layout(R.layout.welcome)
                .colorBackground(getResources().getColor(R.color.colorPrimaryDark))
                .nextQuestionAuto()
                .pageNumber(0)
                .build());

//        addQuestion(new Infor.Config()
//                .title("Sociodemográfico")
//                .description("As próximas perguntas referem-se a você e à sua casa.",
//                        Color.WHITE)
//                .image(R.drawable.x_trophy)
//                .colorBackground(getResources().getColor(R.color.colorPrimary))
//                .nextQuestionAuto()
//                .pageNumber(1)
//                .build());


        String[] q = getResources().getStringArray(R.array.sports_answers);
        ArrayList<String> question = new ArrayList<>();
        Collections.addAll(question, q);
        addQuestion(new MultipleChoice.Config()
                .title(getString(R.string.q1), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .image(R.drawable.x_trophy)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputItems(question)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .nextQuestionAuto()
                .pageNumber(2)
                .build());

        String[] q2 = getResources().getStringArray(R.array.frequency_sports_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q2);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q2), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .image(R.drawable.x_gymnastics)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(3)
                .build());

        addQuestion(new Infor.Config()
                .title("Hábitos Alimentares", Color.WHITE)
                .description("Agora você responderá perguntas sobre seus hábitos alimentares.",
                        Color.WHITE)
                .image(R.drawable.x_diet)
                .colorBackground(getResources().getColor(R.color.colorPrimary))
                .nextQuestionAuto()
                .pageNumber(1)
                .build());

        String[] q3 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q3);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q3), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .image(R.drawable.x_breakfast)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(3)
                .build());

        String[] q4 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q4);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q4), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBrown))
                .image(R.drawable.x_water)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q5 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q5);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q5), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .image(R.drawable.x_chop)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q6 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q7 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q7), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .image(R.drawable.x_salad)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q8 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q8), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .image(R.drawable.x_fries)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q9 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q9), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_milk)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q10 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q10), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorAmber))
                .image(R.drawable.x_beans)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q11 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q11), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .image(R.drawable.x_apple)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q12 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q12), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .image(R.drawable.x_lollipop)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q13 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q13), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .image(R.drawable.x_sausages)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q14 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q14), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorPink))
                .image(R.drawable.x_breastfeeding)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q15 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q15), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .image(R.drawable.x_allergy)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q16 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q16), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_blood_pressure)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q17 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q17), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .image(R.drawable.x_glucometer)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q17 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q18 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q19 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

        String[] q20 = getResources().getStringArray(R.array.frequency_lunch_answers);
        question = new ArrayList<>();
        Collections.addAll(question, q6);
        addQuestion(new SingleChoice.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(question)
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());


//
//        addQuestion(new Open.Config()
//                .title("Title of the question 1", Color.WHITE)
//                .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry?",
//                        Color.WHITE)
//                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
//                .image(R.drawable.placeholder)
//                .buttonClose(R.drawable.ic_action_close_dark)
//                .inputColorBackgroundTint(ContextCompat.getColor(this, R.color.colorAccent))
//                .inputColorText(Color.WHITE)
//                .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
//                .nextQuestionAuto()
//                .pageNumber(1)
//                .build());

//        addQuestion(new Open.Config()
//                .title("Qual o seu nome?", Color.WHITE)
//                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
//                .buttonClose(R.drawable.ic_action_close_dark)
//                .inputBackground(R.drawable.edittext_border_style)
//                .inputColorText(Color.WHITE)
//                .pageNumber(2)
//                .build());
//
//
//        addQuestion(new DichotomicChoice.Config()
//                .title("Title of the question 3", Color.WHITE)
//                .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry?",
//                        Color.WHITE)
//                .colorBackground(ContextCompat.getColor(this, R.color.colorGreen))
//                .buttonClose(R.drawable.ic_action_close_dark)
//                .image(R.drawable.placeholder)
//                .enableZoomImage()
//                .inputStyle(R.drawable.radio_sample1_lef, R.drawable.radio_sample1_right,
//                        Color.WHITE, Color.WHITE)
//                .inputLeftText(R.string.cancel)
//                .inputRightText(R.string.day)
//                .pageNumber(3)
//                .build());
////
//        addQuestion(new SingleChoice.Config()
//                .title("Title of the question 4", Color.WHITE)
//                .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry?",
//                        Color.WHITE)
//                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
//                .image(R.drawable.placeholder)
//                .buttonClose(R.drawable.ic_action_close_dark)
//                .inputColorBackgroundTint(Color.WHITE)
//                .inputColorSelectedText(Color.WHITE)
//                .inputItems(new ArrayList<String>() {{
//                    add("Item 1");
//                    add("Item 2");
//                    add("Item 3");
//                    add("Item 4");
//                }})
//                .inputDisableAddNewItem()
//                .nextQuestionAuto()
//                .pageNumber(4)
//                .build());
////
//        addQuestion(new DichotomicChoice.Config()
//                .title("Title of the question 5")
//                .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry?")
//                .nextQuestionAuto()
//                .pageNumber(3)
//                .build());
//


        addQuestion(new Infor.Config()
                .title("Thank you for the answers :)")
                .pageNumber(-1)
                .build());
    }

    @Override
    public void onClosePage() {
        new AlertDialog
                .Builder(this)
                .setMessage("Do you want to cancel the survey??")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onAnswerInfo(int page) {
        Log.d(LOG_TAG, "onAnswerInfo() | PAGE: " + page);
        if (page == -1) { // end page
            finish();
        }
    }

    @Override
    public void onAnswerDichotomic(int page, boolean value) {
        Log.d(LOG_TAG, "onAnswerDichotomic() | PAGE:  " + page + " | ANSWER: " + value);
    }

    @Override
    public void onAnswerSingle(int page, String value, int indexValue) {
//        Log.d(LOG_TAG, "onAnswerMultiple() | PAGE:  " + page
//                + " | ANSWER (value): " + value
//                + " | ANSWER (index): " + indexValue);
    }

    @Override
    public void onAnswerMultiple(int page, List<String> values, List<Integer> indexValues) {
//        Log.d(LOG_TAG, "onAnswerMultiple() | PAGE:  " + page
//                + " | ANSWER (values): " + Arrays.toString(values.toArray())
//                + " | ANSWER (indexes): " + Arrays.toString(indexValues.toArray()));
    }

    @Override
    public void onAnswerTextBox(int page, String value) {
//        Log.d(LOG_TAG, "onAnswerTextBox() | PAGE:  " + page
//                + " | ANSWER: " + value);
    }
}
