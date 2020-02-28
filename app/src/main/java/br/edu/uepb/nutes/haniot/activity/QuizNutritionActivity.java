package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import br.edu.uepb.nutes.haniot.data.model.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.NutritionalQuestionnaireType;
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
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.simplesurvey.base.SimpleSurvey;
import br.edu.uepb.nutes.simplesurvey.question.Dichotomic;
import br.edu.uepb.nutes.simplesurvey.question.Infor;
import br.edu.uepb.nutes.simplesurvey.question.Multiple;
import br.edu.uepb.nutes.simplesurvey.question.Open;
import br.edu.uepb.nutes.simplesurvey.question.Single;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;

/**
 * QuizNutritionActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class QuizNutritionActivity extends SimpleSurvey implements Infor.OnInfoListener,
        Dichotomic.OnDichotomicListener, Single.OnSingleListener,
        Multiple.OnMultipleListener,
        Open.OnTextBoxListener {

    private final String LOG_TAG = QuizNutritionActivity.class.getSimpleName();
    private final int FIRST_PAGE = 0;
    private final int END_PAGE = -1;
    private final int CATEGORY_PHYSICAL_ACTIVITIES = 1;
    private final int CATEGORY_FEENDING_HABITS = 2;
    private final int CATEGORY_MEDICAL_RECORDS = 3;
    private final int CATEGORY_SLEEP_HABITS = 4;
    private Patient patient;

    private PhysicalActivityHabit physicalActivityHabits;
    private FeedingHabitsRecord feedingHabitsRecord;
    private MedicalRecord medicalRecord;
    private List<ChronicDisease> chronicDiseases;
    private SleepHabit sleepHabit;
    private List<WeeklyFoodRecord> weeklyFoodRecords;

    private FeedingHabitsDAO feedingHabitsDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private SleepHabitsDAO sleepHabitsDAO;
    private PhysicalActivityHabitsDAO physicalActivityHabitsDAO;

    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;
    private CompositeDisposable compositeDisposable;
    int checkpoint;
    private NutritionalQuestionnaire nutritionalQuestionnaire;
    private String updateType;
    private String idUpdate;
    private Object resourceToUpdate;

    /**
     * Init view.
     */
    @Override
    protected void initView() {
        initResources();

        checkpoint = getIntent().getIntExtra("checkpoint", -1);
        idUpdate = getIntent().getStringExtra("idUpdate");
        setMessageBlocked(getResources().getString(R.string.not_answered));
        // Animation
        setFadeAnimation();

        switch (checkpoint) {
            case MEDICAL_RECORDS:
                addMedicalRocordsPages();
                updateType = NutritionalQuestionnaireType.MEDICAL_RECORDS;
                break;
            case PHYSICAL_ACTIVITY:
                addPhysicalHabitsPages();
                updateType = NutritionalQuestionnaireType.PHYSICAL_ACTIVITY_HABITS;
                break;
            case FEEDING_HABITS:
                addFeedingHabitsPages();
                updateType = NutritionalQuestionnaireType.FEEDING_HABITS_RECORD;
                break;
            case SLEEP_HABITS:
                addSleepHabitsPages();
                updateType = NutritionalQuestionnaireType.SLEEP_HABIT;
                break;
            default:
                addStartPage();
                addPhysicalHabitsPages();
                addFeedingHabitsPages();
                addMedicalRocordsPages();
                addSleepHabitsPages();
        }
        addEndPage();
    }

    /**
     * Init resources.
     */
    private void initResources() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        compositeDisposable = new CompositeDisposable();
        patient = appPreferencesHelper.getLastPatient();
        chronicDiseases = new ArrayList<>();
        weeklyFoodRecords = new ArrayList<>();
        feedingHabitsRecord = new FeedingHabitsRecord();
        medicalRecord = new MedicalRecord();
        sleepHabit = new SleepHabit();
        physicalActivityHabits = new PhysicalActivityHabit();
        feedingHabitsDAO = FeedingHabitsDAO.getInstance(this);
        physicalActivityHabitsDAO = PhysicalActivityHabitsDAO.getInstance(this);
        sleepHabitsDAO = SleepHabitsDAO.getInstance(this);
        medicalRecordDAO = MedicalRecordDAO.getInstance(this);
        nutritionalQuestionnaire = new NutritionalQuestionnaire();
    }

    /**
     * Save medical records in server.
     */
    private void saveMedicalRecords() {
        medicalRecord.setChronicDiseases(chronicDiseases);
        medicalRecord.setChronicDiseasesDB(chronicDiseases);
        medicalRecord.setPatientId(patient.get_id());
        Log.i(LOG_TAG, medicalRecord.toJson());
        medicalRecordDAO.save(medicalRecord);
        nutritionalQuestionnaire.setMedicalRecord(medicalRecord);
        resourceToUpdate = medicalRecord;
    }

    /**
     * Save feeding habits in server.
     */
    private void saveFeedingHabits() {
        feedingHabitsRecord.setPatientId(patient.get_id());
        feedingHabitsRecord.setWeeklyFeedingHabitsDB(weeklyFoodRecords);
        feedingHabitsRecord.setWeeklyFeedingHabits(weeklyFoodRecords);
        Log.i(LOG_TAG, feedingHabitsRecord.toJson());
        feedingHabitsDAO.save(feedingHabitsRecord);
        nutritionalQuestionnaire.setFeedingHabitsRecord(feedingHabitsRecord);
        resourceToUpdate = feedingHabitsRecord;
    }

    /**
     * Save sleep habits in server.
     */
    private void saveSleepHabits() {
        sleepHabit.setPatientId(patient.get_id());
        Log.i(LOG_TAG, sleepHabit.toJson());
        sleepHabitsDAO.save(sleepHabit);
        nutritionalQuestionnaire.setSleepHabit(sleepHabit);
        resourceToUpdate = sleepHabit;
    }

    /**
     * Save activity habits in server.
     */
    private void saveActivityHabits() {
        physicalActivityHabits.setPatientId(patient.get_id());
        Log.i(LOG_TAG, physicalActivityHabits.toJson());
        physicalActivityHabitsDAO.save(physicalActivityHabits);
        nutritionalQuestionnaire.setPhysicalActivityHabit(physicalActivityHabits);
        resourceToUpdate = physicalActivityHabits;
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

    private void addMedicalRocordsPages() {

        //CATEGORY 4
        addQuestion(new Infor.Config()
                .title(R.string.category_4, Color.WHITE)
                .titleTextSize(28)
                .buttonClose(R.drawable.ic_action_close_dark)
                .description(R.string.category_4_desc,
                        Color.WHITE)
                .image(R.drawable.x_drug)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_MEDICAL_RECORDS)
                .build());


        addQuestion(new Single.Config()
                .title(getString(R.string.q16), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.x_blood_pressure)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_or_no_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(19)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q17), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .description("")
                .image(R.drawable.x_glucosemeter)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_or_no_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(20)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q18), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .description("")
                .image(R.drawable.x_blood)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.yes_or_no_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(21)
                .build());

    }

    private void addFeedingHabitsPages() {

        //CATEGORY 3
        addQuestion(new Infor.Config()
                .title(R.string.category_3, Color.WHITE)
                .titleTextSize(28)
                .buttonClose(R.drawable.ic_action_close_dark)
                .description(R.string.category_3_desc,
                        Color.WHITE)
                .description("")
                .image(R.drawable.x_diet)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_FEENDING_HABITS)
                .build());


        addQuestion(new Single.Config()
                .title(getString(R.string.q3), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .description("")
                .image(R.drawable.x_breakfast)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_lunch_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(6)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q4), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .description("")
                .image(R.drawable.x_water)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_water_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(7)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q5), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .description("")
                .image(R.drawable.x_beef)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_beef_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(8)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q6), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.x_cola)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_soda_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(9)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q7), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorOrange))
                .description("")
                .image(R.drawable.x_salad)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_salad_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(10)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q8), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .description("")
                .image(R.drawable.x_fries)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_freats_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(11)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q9), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.x_milk)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_milk_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(12)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q10), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorAmber))
                .description("")
                .image(R.drawable.x_beans)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_beans_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(13)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q11), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorCyan))
                .description("")
                .image(R.drawable.x_apple)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_fruits_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(14)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q12), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorDeepPurple))
                .description("")
                .image(R.drawable.x_lollipop)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_goodies_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(15)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q13), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .description("")
                .image(R.drawable.x_sausages)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_sausages_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(16)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q14), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorPink))
                .description("")
                .image(R.drawable.x_breastfeeding)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.breast_feeding_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(17)
                .build());

        addQuestion(new Multiple.Config()
                .title(getString(R.string.q15), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorTeal))
                .description("")
                .image(R.drawable.x_allergy)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputItems(parseAnswers(R.array.allergy_answers))
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .nextQuestionAuto()
                .inputDisableAddNewItem()
                .pageNumber(18)
                .build());

    }

    private void addPhysicalHabitsPages() {
        //CATEGORY 2
        addQuestion(new Infor.Config()
                .title(R.string.category_2, Color.WHITE)
                .titleTextSize(28)
                .buttonClose(R.drawable.ic_action_close_dark)
                .description(R.string.category_2_desc, Color.WHITE)
                .descriptionTextSize(14)
                .image(R.drawable.x_sneaker)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_PHYSICAL_ACTIVITIES)
                .build());


        addQuestion(new Multiple.Config()
                .title(getString(R.string.q1), Color.WHITE)
                .titleTextSize(28)
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

        addQuestion(new Single.Config()
                .title(getString(R.string.q2), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorRed))
                .description("")
                .image(R.drawable.x_gymnastics)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.frequency_sports_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(5)
                .build());

    }

    private void addSleepHabitsPages() {

        //CATEGORY 5
        addQuestion(new Infor.Config()
                .title(R.string.category_5, Color.WHITE)
                .titleTextSize(28)
                .description(R.string.category_5_desc,
                        Color.WHITE)
                .image(R.drawable.x_bed)
                .buttonClose(R.drawable.ic_action_close_dark)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_SLEEP_HABITS)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q19), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlueGrey))
                .description("")
                .image(R.drawable.x_sleep)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.hours_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(22)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.q20), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .description("")
                .image(R.drawable.x_wakeup)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.hours_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(23)
                .build());
    }

    /**
     * Construct quiz.
     */
    private void addStartPage() {
        setMessageBlocked(getResources().getString(R.string.not_answered));

        // Animation
        setFadeAnimation();

        //INTRO PAGE
        addQuestion(new Infor.Config()
                .layout(R.layout.welcome_nutrition_quiz)
                .colorBackground(getResources().getColor(R.color.colorPrimaryDark))
                .nextQuestionAuto()
                .pageNumber(FIRST_PAGE)
                .build());

    }

    private void addEndPage() {
        //END PAGE
        addQuestion(new Infor.Config()
                .title(R.string.thank_you, Color.WHITE)
                .titleTextSize(28)
                .description(R.string.nutrition_final_instructions)
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
        if (page == CATEGORY_FEENDING_HABITS && checkpoint == -1) {
            saveActivityHabits();
        } else if (page == CATEGORY_MEDICAL_RECORDS && checkpoint == -1) {
            saveFeedingHabits();
        } else if (page == CATEGORY_SLEEP_HABITS && checkpoint == -1) {
            saveMedicalRecords();
        } else if (page == END_PAGE) {
            switch (checkpoint) {
                case MEDICAL_RECORDS:
                    saveMedicalRecords();
                    break;
                case PHYSICAL_ACTIVITY:
                    saveActivityHabits();
                    break;
                case FEEDING_HABITS:
                    saveFeedingHabits();
                    break;
                case SLEEP_HABITS:
                    saveSleepHabits();
                    break;
                default:
                    saveSleepHabits();
            }
            sendQuestionnaireToServer();
        }
    }

    private void sendQuestionnaireToServer() {
        Log.w("AAA", "sendQuestionnaireToServer");
        ProgressDialog dialog = ProgressDialog.show(this, "Sincronização",
                "Aguarde alguns instantes...", true);
        dialog.show();

        if (updateType == null) {
            Log.w("AAA", "updateType == null");
            Log.w("AAA", "Saving: " + nutritionalQuestionnaire.toJson());
            compositeDisposable.add(haniotNetRepository
                    .saveNutritionalQuestionnaire(patient.get_id(), nutritionalQuestionnaire)
                    .doAfterTerminate(() -> {
                    })
                    .subscribe(nutritionalQuestionnaire -> {
                        dialog.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Salvo com sucesso!");
                        builder.setCancelable(true);
                        builder.setNeutralButton("Ok", (dialog1, which) -> {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        });
                        builder.show();
                    }, throwable -> {
                        Log.w("AAA", throwable.getMessage());
                        dialog.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Não foi possível concluir a operação...");
                        builder.setMessage("Tente novamente mais tarde!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", (dialog12, which) -> {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                            dialog12.cancel();
                        });
                        builder.show();
                    }));
        } else {
            Log.w("AAA", "updateType != null: " + updateType);
            Log.w("AAA", "updateType: " + updateType + " idUpdate: " + idUpdate);
            printJson();
            Log.w("AAA", "id: " + idUpdate);
            if (idUpdate != null) {
                compositeDisposable.add(haniotNetRepository
                        .updateNutritionalQuestionnaire(patient.get_id(), idUpdate, updateType, resourceToUpdate)
                        .subscribe(o -> {
                            dialog.cancel();
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Atualizado com sucesso!");
                            builder.setCancelable(true);
                            builder.setNeutralButton("Ok", (dialog1, which) -> {
                                finish();
                            });
                            builder.show();
                        }, throwable -> {
                            Log.w("AAA", throwable.getMessage());
                            dialog.cancel();
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Não foi possível concluir a operação...");
                            builder.setMessage("Tente novamente mais tarde!");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Ok", (dialog12, which) -> {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                                dialog12.cancel();
                            });
                            builder.show();
                        }));
            }
        }
    }

    private void printJson() {
        if (resourceToUpdate instanceof SleepHabit) {
            Log.w("AAA", ((SleepHabit) resourceToUpdate).toJson());
        } else if (resourceToUpdate instanceof MedicalRecord) {
            Log.w("AAA", ((MedicalRecord) resourceToUpdate).toJson());
        } else if (resourceToUpdate instanceof PhysicalActivityHabit) {
            Log.w("AAA", ((PhysicalActivityHabit) resourceToUpdate).toJson());
        } else if (resourceToUpdate instanceof FeedingHabitsRecord) {
            Log.w("AAA", ((FeedingHabitsRecord) resourceToUpdate).toJson());
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
                physicalActivityHabits.setSchoolActivityFreq(SchoolActivityFrequencyType
                        .getString(indexValue));
                break;
            case 6:
                feedingHabitsRecord.setBreakfastDailyFrequency(FeendingHabitsRecordType
                        .DailyFeedingFrequency.getString(indexValue));
                break;
            case 7:
                feedingHabitsRecord.setDailyWaterGlasses(FeendingHabitsRecordType
                        .OneDayFeedingAmount.getString(indexValue));
                break;
            case 8:
                WeeklyFoodRecord weeklyFoodRecord = new WeeklyFoodRecord();
                weeklyFoodRecord.setFood(FoodType.FISH_CHICKEN_MEAT);
                weeklyFoodRecord.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord);
                break;
            case 9:
                WeeklyFoodRecord weeklyFoodRecord1 = new WeeklyFoodRecord();
                weeklyFoodRecord1.setFood(FoodType.SODA);
                weeklyFoodRecord1.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord1);
                break;
            case 10:
                WeeklyFoodRecord weeklyFoodRecord2 = new WeeklyFoodRecord();
                weeklyFoodRecord2.setFood(FoodType.SALAD_VEGETABLE);
                weeklyFoodRecord2.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord2);
                break;
            case 11:
                WeeklyFoodRecord weeklyFoodRecord3 = new WeeklyFoodRecord();
                weeklyFoodRecord3.setFood(FoodType.FRIED_SALT_FOOD);
                weeklyFoodRecord3.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord3);
                break;
            case 12:
                WeeklyFoodRecord weeklyFoodRecord4 = new WeeklyFoodRecord();
                weeklyFoodRecord4.setFood(FoodType.MILK);
                weeklyFoodRecord4.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord4);
                break;
            case 13:
                WeeklyFoodRecord weeklyFoodRecord5 = new WeeklyFoodRecord();
                weeklyFoodRecord5.setFood(FoodType.BEAN);
                weeklyFoodRecord5.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord5);
                break;
            case 14:
                WeeklyFoodRecord weeklyFoodRecord6 = new WeeklyFoodRecord();
                weeklyFoodRecord6.setFood(FoodType.FRUITS);
                weeklyFoodRecord6.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord6);
                break;
            case 15:
                WeeklyFoodRecord weeklyFoodRecord7 = new WeeklyFoodRecord();
                weeklyFoodRecord7.setFood(FoodType.CANDY_SUGAR_COOKIE);
                weeklyFoodRecord7.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord7);
                break;
            case 16:
                WeeklyFoodRecord weeklyFoodRecord8 = new WeeklyFoodRecord();
                weeklyFoodRecord8.setFood(FoodType.BURGER_SAUSAGE);
                weeklyFoodRecord8.setSevenDaysFreq(FeendingHabitsRecordType
                        .SevenDaysFeedingFrequency.getString(indexValue));
                weeklyFoodRecords.add(weeklyFoodRecord8);
                break;
            case 17:
                feedingHabitsRecord.setSixMonthBreastFeeding(FeendingHabitsRecordType
                        .BreastFeeding.getString(indexValue));
                break;
            case 19:
                ChronicDisease chronicDisease = new ChronicDisease();
                chronicDisease.setType(ChronicDiseaseType.ChronicDisease.HYPERTENSION);
                chronicDisease.setDiseaseHistory(ChronicDiseaseType
                        .DisieaseHistory.getString(indexValue));
                chronicDiseases.add(chronicDisease);
                break;
            case 20:
                ChronicDisease chronicDisease2 = new ChronicDisease();
                chronicDisease2.setType(ChronicDiseaseType.ChronicDisease.DIABETES);
                chronicDisease2.setDiseaseHistory(ChronicDiseaseType
                        .DisieaseHistory.getString(indexValue));
                chronicDiseases.add(chronicDisease2);
                break;
            case 21:
                ChronicDisease chronicDisease3 = new ChronicDisease();
                chronicDisease3.setType(ChronicDiseaseType.ChronicDisease.BLOOD_FAT);
                chronicDisease3.setDiseaseHistory(ChronicDiseaseType
                        .DisieaseHistory.getString(indexValue));
                chronicDiseases.add(chronicDisease3);
                break;
            case 22:
                sleepHabit.setWeekDaySleep(indexValue);
                break;
            case 23:
                sleepHabit.setWeekDayWakeUp(indexValue);
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

        if (page == 4) {
            List<String> answers = new ArrayList<>();

            for (Integer integer : indexValues) {
                answers.add(SportsType.getString(integer));
            }
            physicalActivityHabits.setWeeklyActivities(answers);
        } else if (page == 18) {
            List<String> answers2 = new ArrayList<>();

            for (Integer integer : indexValues) {
                answers2.add(FeendingHabitsRecordType.FoodAllergyStringolerance.getString(integer));
            }
            feedingHabitsRecord.setFoodAllergyIntolerance(answers2);
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
        compositeDisposable.dispose();
    }
}
