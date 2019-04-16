package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.ChronicDiseaseType;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeendingHabitsRecordType;
import br.edu.uepb.nutes.haniot.data.model.FoodType;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SchoolActivityFrequencyType;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.SportsType;
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
import br.edu.uepb.nutes.haniot.data.model.dao.FeedingHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MedicalRecordDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PhysicalActivityHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.SleepHabitsDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.simplesurvey.base.SimpleSurvey;
import br.edu.uepb.nutes.simplesurvey.question.Dichotomic;
import br.edu.uepb.nutes.simplesurvey.question.Infor;
import br.edu.uepb.nutes.simplesurvey.question.Multiple;
import br.edu.uepb.nutes.simplesurvey.question.Open;
import br.edu.uepb.nutes.simplesurvey.question.Single;
import retrofit2.HttpException;

/**
 * PatientQuizActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class PatientQuizOdontoActivity extends SimpleSurvey implements Infor.OnInfoListener,
        Dichotomic.OnDichotomicListener, Single.OnSingleListener,
        Multiple.OnMultipleListener,
        Open.OnTextBoxListener {

    private final String LOG_TAG = PatientQuizOdontoActivity.class.getSimpleName();
    private final int FIRST_PAGE = 0;
    private final int END_PAGE = -1;
    private final int CATEGORY_PHYSICAL_ACTIVITIES = 1;
    private final int CATEGORY_FEENDING_HABITS = 2;
    private final int CATEGORY_MEDICAL_RECORDS = 3;
    private final int CATEGORY_SLEEP_HABITS = 4;
    private Patient patient;

    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;

    /**
     * Init view.
     */
    @Override
    protected void initView() {
        initResources();
        addPages();
    }

    /**
     * Init resources.
     */
    private void initResources() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);
            Log.i(LOG_TAG, httpEx.getMessage());
        }
        // message 500
    }

    /**
     * Construct quiz.
     */
    private void addPages() {
        setMessageBlocked(getResources().getString(R.string.not_answered));

        // Animation
        setFadeAnimation();

        //INTRO PAGE
        addQuestion(new Infor.Config()
                .layout(R.layout.welcome)
                .colorBackground(getResources().getColor(R.color.colorPrimaryDark))
                .nextQuestionAuto()
                .pageNumber(FIRST_PAGE)
                .build());
//
//        //CATEGORY 2
//        addQuestion(new Infor.Config()
//                .title(R.string.category_2, Color.WHITE)
//                .titleTextSize(28)
//                .description(R.string.category_2_desc, Color.WHITE)
//                .descriptionTextSize(14)
//                .image(R.drawable.x_sneaker)
//                .colorBackground(getResources().getColor(R.color.colorAccent))
//                .inputText(R.string.bt_next)
//                .buttonBackground(R.drawable.button_stylezed)
//                .nextQuestionAuto()
//                .pageNumber(CATEGORY_PHYSICAL_ACTIVITIES)
//                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_1), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .description("")
                .image(R.drawable.z_help)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .nextQuestionAuto()
                .inputDisableAddNewItem()
                .pageNumber(4)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_2), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.z_friend)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

//        //CATEGORY 3
//        addQuestion(new Infor.Config()
//                .title(R.string.category_3, Color.WHITE)
//                .titleTextSize(28)
//                .description(R.string.category_3_desc,
//                        Color.WHITE)
//                .description("")
//                .image(R.drawable.x_diet)
//                .colorBackground(getResources().getColor(R.color.colorAccent))
//                .inputText(R.string.bt_next)
//                .buttonBackground(R.drawable.button_stylezed)
//                .nextQuestionAuto()
//                .pageNumber(CATEGORY_FEENDING_HABITS)
//                .build());


        addQuestion(new Single.Config()
                .title(getString(R.string.question_4), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .description("")
                .image(R.drawable.z_new_friend)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(6)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_5), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .description("")
                .image(R.drawable.z_time)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(7)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_6), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .description("")
                .image(R.drawable.z_proximity_family)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(8)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_7), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.z_activity)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(9)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_8), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .description("")
                .image(R.drawable.z_familiy_activity_easy)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(10)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_9), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .description("")
                .image(R.drawable.z_conselho)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(11)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_10), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.z_union_family)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(12)
                .build());

        addQuestion(new Open.Config()
                .title(getString(R.string.question_11), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorAmber))
                .description("")
                .image(R.drawable.z_coesion)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .nextQuestionAuto()
                .pageNumber(13)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_12), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .description("")
                .image(R.drawable.z_color)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.race_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(14)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_13), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .description("")
                .image(R.drawable.z_scholarity)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.scholarity_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(15)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_14), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .description("")
                .image(R.drawable.z_house)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.number6_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(16)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_15), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorPink))
                .description("")
                .image(R.drawable.z_toothbrush)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.number3_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(17)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_16), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .description("")
                .image(R.drawable.z_caries_mil)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputItems(parseAnswers(R.array.yes_not_answers))
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .nextQuestionAuto()
                .inputDisableAddNewItem()
                .pageNumber(18)
                .build());
//
//        //CATEGORY 4
//        addQuestion(new Infor.Config()
//                .title(R.string.category_4, Color.WHITE)
//                .titleTextSize(28)
//                .description(R.string.category_4_desc,
//                        Color.WHITE)
//                .image(R.drawable.x_drug)
//                .colorBackground(getResources().getColor(R.color.colorAccent))
//                .inputText(R.string.bt_next)
//                .buttonBackground(R.drawable.button_stylezed)
//                .nextQuestionAuto()
//                .pageNumber(CATEGORY_MEDICAL_RECORDS)
//                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_17), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.z_caries_mil)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_not_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(19)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_18), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .description("")
                .image(R.drawable.z_caries_white)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_not_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(20)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_19), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .description("")
                .image(R.drawable.z_caries_white)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_not_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(21)
                .build());

//        //CATEGORY 5
//        addQuestion(new Infor.Config()
//                .title(R.string.category_5, Color.WHITE)
//                .titleTextSize(28)
//                .description(R.string.category_5_desc,
//                        Color.WHITE)
//                .image(R.drawable.x_bed)
//                .colorBackground(getResources().getColor(R.color.colorAccent))
//                .inputText(R.string.bt_next)
//                .buttonBackground(R.drawable.button_stylezed)
//                .nextQuestionAuto()
//                .pageNumber(CATEGORY_SLEEP_HABITS)
//                .build());

        //END PAGE
        addQuestion(new Infor.Config()
                .title(R.string.thank_you, Color.WHITE)
                .titleTextSize(28)
                .description(R.string.final_instructions)
                .descriptionTextSize(14)
                .descriptionColor(Color.WHITE)
                .colorBackground(getResources().getColor(R.color.colorPrimaryDark))
                .image(R.drawable.x_like)
                .buttonClose(R.drawable.ic_action_close_dark)
                .buttonColorText(getResources().getColor(R.color.colorPrimaryDark))
                .buttonBackground(R.drawable.button_stylezed)
                .pageNumber(END_PAGE)
                .build());
    }

    /**
     * Parse answers to constants.
     *
     * @param id
     * @return
     */
    public List<String> parseAnswers(int id) {
        ArrayList<String> answers = new ArrayList<>();
        String[] parseAnswers = getResources().getStringArray(id);
        Collections.addAll(answers, parseAnswers);
        return answers;
    }

    /**
     * Cancel quiz.
     */
    @Override
    public void onClosePage() {
        new AlertDialog
                .Builder(this)
                .setMessage(getResources().getString(R.string.cancel))
                .setPositiveButton(getResources().getText(R.string.yes_text), (dialog, which) -> {
                    startActivity(new Intent(this, ManagerPatientsActivity.class));
                    finish();
                })
                .setNegativeButton(getResources().getText(R.string.no_text), null)
                .show();
    }

    /**
     * Get answer of info page.
     *
     * @param page
     */
    @Override
    public void onAnswerInfo(int page) {
        Log.d(LOG_TAG, "onAnswerInfo() | PAGE: " + page);
        switch (page) {
            case CATEGORY_PHYSICAL_ACTIVITIES:

                break;
            case CATEGORY_FEENDING_HABITS:
                break;
            case CATEGORY_MEDICAL_RECORDS:
                break;
            case CATEGORY_SLEEP_HABITS:
                break;
            case END_PAGE:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    /**
     * Get answers of dichotomic.
     *
     * @param page
     * @param value
     */
    @Override
    public void onAnswerDichotomic(int page, boolean value) {
        Log.d(LOG_TAG, "onAnswerDichotomic() | PAGE:  " + page + " | ANSWER: " + value);
    }

    /**
     * Get answers of single.
     *
     * @param page
     * @param value
     * @param indexValue
     */
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

    /**
     * Get answers of multiple.
     *
     * @param page
     * @param values
     * @param indexValues
     */
    @Override
    public void onAnswerMultiple(int page, List<String> values, List<Integer> indexValues) {
        Log.d(LOG_TAG, "onAnswerMultiple() | PAGE:  " + page
                + " | ANSWER (values): " + Arrays.toString(values.toArray())
                + " | ANSWER (indexes): " + Arrays.toString(indexValues.toArray()));

        switch (page) {
            case 4:
                List<String> strings = new ArrayList<>();

                for (Integer integer : indexValues) {
                    strings.add(SportsType.getString(integer));
                }
                break;
            case 18:
                List<String> strings2 = new ArrayList<>();

                for (Integer integer : indexValues) {
                    strings2.add(FeendingHabitsRecordType.FoodAllergyStringolerance.getString(integer));
                }
                break;
        }
    }

    /**
     * Get answers of text box.
     *
     * @param page
     * @param value
     */
    @Override
    public void onAnswerTextBox(int page, String value) {
        Log.d(LOG_TAG, "onAnswerTextBox() | PAGE:  " + page
                + " | ANSWER: " + value);
    }

    /**
     * On destroy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
    }
}
