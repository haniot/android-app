package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.model.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.model.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.model.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.model.ToothLesion;
import br.edu.uepb.nutes.haniot.data.model.type.FrequencyAnswersType;
import br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType;
import br.edu.uepb.nutes.haniot.data.model.type.SociodemographicType;
import br.edu.uepb.nutes.haniot.data.model.type.ToothLesionType;
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

import static br.edu.uepb.nutes.haniot.data.model.type.TypeEvaluation.FAMILY_COHESION;
import static br.edu.uepb.nutes.haniot.data.model.type.TypeEvaluation.ORAL_HEALTH;
import static br.edu.uepb.nutes.haniot.data.model.type.TypeEvaluation.SOCIODEMOGRAPHICS;

/**
 * QuizOdontologyActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class QuizOdontologyActivity extends SimpleSurvey implements Infor.OnInfoListener,
        Dichotomic.OnDichotomicListener, Single.OnSingleListener,
        Multiple.OnMultipleListener,
        Open.OnTextBoxListener {

    private final String LOG_TAG = QuizOdontologyActivity.class.getSimpleName();
    private final int FIRST_PAGE = 0;
    private final int END_PAGE = -1;
    private final int GATEGORY_FAMILY_COHESION = -2;
    private final int CATEGORY_SOCIODEMOGRAPHIC = -3;
    private final int CATEGORY_ORAL_HEALTH = -4;
    private Patient patient;

    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;

    private FamilyCohesionRecord familyCohesionRecord;
    private OralHealthRecord oralHealthRecord;
    private SociodemographicRecord sociodemographicRecord;
    private List<ToothLesion> toothLesions;
    private List<Integer> points;
    private OdontologicalQuestionnaire odontologicalQuestionnaire;
    int checkpoint;
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
            case FAMILY_COHESION:
                setFamilyCohesion();
                updateType = OdontologicalQuestionnaireType.FAMILY_COHESION_RECORD;
                break;
            case ORAL_HEALTH:
                setOralRecord();
                updateType = OdontologicalQuestionnaireType.ORAL_HEALTH_RECORD;
                break;
            case SOCIODEMOGRAPHICS:
                setSociodemographicRecord();
                updateType = OdontologicalQuestionnaireType.SOCIODEMOGRAPHIC_RECORD;
                break;
            default:
                addStartPage();
                setFamilyCohesion();
                setOralRecord();
                setSociodemographicRecord();
        }
        addEndPage();
    }

    private void addStartPage() {

        //INTRO PAGE
        addQuestion(new Infor.Config()
                .layout(R.layout.welcome_odontology_quiz)
                .buttonClose(R.drawable.ic_action_close_dark)
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
                .description(R.string.odontology_final_instructions)
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
     * Init resources.
     */
    private void initResources() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();
        familyCohesionRecord = new FamilyCohesionRecord();
        oralHealthRecord = new OralHealthRecord();
        sociodemographicRecord = new SociodemographicRecord();
        toothLesions = new ArrayList<>();
        points = new ArrayList<>();
        odontologicalQuestionnaire = new OdontologicalQuestionnaire();
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

    private void saveFamilyCohesion() {
        familyCohesionRecord.setPatientId(patient.get_id());
        int totalPoints = 0;
        Log.w("AAA", "Size points: " + points.size());
        for (Integer integer : points) {
            totalPoints += integer;
            Log.w("AAA", "point: " + integer + " - " + totalPoints);
        }
        familyCohesionRecord.setFamilyCohesionResult(totalPoints);
        familyCohesionRecord.toJson();
        odontologicalQuestionnaire.setFamilyCohesionRecord(familyCohesionRecord);
        resourceToUpdate = familyCohesionRecord;
    }

    private void saveOralHealth() {
        oralHealthRecord.setPatientId(patient.get_id());
        oralHealthRecord.setToothLesions(toothLesions);
        oralHealthRecord.toJson();
        odontologicalQuestionnaire.setOralHealthRecord(oralHealthRecord);
        resourceToUpdate = oralHealthRecord;
    }

    private void saveSociodemographic() {
        sociodemographicRecord.setPatientId(patient.get_id());
        sociodemographicRecord.toJson();
        odontologicalQuestionnaire.setSociodemographicRecord(sociodemographicRecord);
        resourceToUpdate = sociodemographicRecord;
    }

    private void setOralRecord() {

        //CATEGORY OralHealthRecordOB
        addQuestion(new Infor.Config()
                .title(R.string.category_oralhealth, Color.WHITE)
                .titleTextSize(28)
                .description(R.string.category_oralhealth_desc, Color.WHITE)
                .descriptionTextSize(20)
                .image(R.drawable.category_tooth)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonClose(R.drawable.ic_action_close_dark)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_ORAL_HEALTH)
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
                .pageNumber(14)
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
                .pageNumber(15)
                .build());

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
                .pageNumber(16)
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
                .pageNumber(17)
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
                .pageNumber(18)
                .build());

    }

    private void setSociodemographicRecord() {

        //CATEGORY SociodemographicRecordOB
        addQuestion(new Infor.Config()
                .title(R.string.category_sociodemographic, Color.WHITE)
                .titleTextSize(28)
                .description(R.string.category_sociodemographic_desc, Color.WHITE)
                .descriptionTextSize(20)
                .image(R.drawable.category_socio)
                .buttonClose(R.drawable.ic_action_close_dark)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonClose(R.drawable.ic_action_close_dark)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(CATEGORY_SOCIODEMOGRAPHIC)
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
                .pageNumber(11)
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
                .pageNumber(12)
                .build());

        addQuestion(new Open.Config()
                .title(getString(R.string.question_14), Color.WHITE)
                .titleTextSize(28)
                .inputColorText(Color.WHITE)
                .colorBackground(ContextCompat.getColor(this, R.color.colorBlue))
                .description("")
                .image(R.drawable.z_house)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .nextQuestionAuto()
                .pageNumber(13)
                .build());

    }

    private void setFamilyCohesion() {

        //CATEGORY FAMILY_COHESION
        addQuestion(new Infor.Config()
                .title(R.string.category_family_cohesion, Color.WHITE)
                .titleTextSize(28)
                .description(R.string.category_family_cohesion_desc, Color.WHITE)
                .descriptionTextSize(20)
                .image(R.drawable.category_family)
                .buttonClose(R.drawable.ic_action_close_dark)
                .colorBackground(getResources().getColor(R.color.colorAccent))
                .inputText(R.string.bt_next)
                .buttonBackground(R.drawable.button_stylezed)
                .nextQuestionAuto()
                .pageNumber(GATEGORY_FAMILY_COHESION)
                .build());

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
                .pageNumber(1)
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
                .pageNumber(2)
                .build());

        addQuestion(new Single.Config()
                .title(getString(R.string.question_3), Color.WHITE)
                .titleTextSize(28)
                .colorBackground(ContextCompat.getColor(this, R.color.colorAmber))
                .description("")
                .image(R.drawable.z_only_family)
                .buttonClose(R.drawable.ic_action_close_dark)
                .inputColorBackgroundTint(Color.WHITE)
                .inputColorSelectedText(Color.WHITE)
                .inputItems(parseAnswers(R.array.default_answers))
                .inputDisableAddNewItem()
                .nextQuestionAuto()
                .pageNumber(3)
                .build());

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
                .pageNumber(4)
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
                .pageNumber(5)
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
                .pageNumber(6)
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
                .pageNumber(7)
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
                .pageNumber(8)
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
                .pageNumber(9)
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
                .pageNumber(10)
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

    private void setPoint(int index, int value) {
        Log.w("AAA", "Salvando " + value + " in index " + (index - 1));
        points.add(index - 1, value + 1);
        for (Integer integer : points)
            Log.w("AAA", "Salvo " + integer);
    }

    /**
     * Get answer of info page.
     *
     * @param page
     */
    @Override
    public void onAnswerInfo(int page) {

        Log.d(LOG_TAG, "onAnswerInfo() | PAGE: " + page);
        if (page == GATEGORY_FAMILY_COHESION && checkpoint == -1) {
        } else if (page == CATEGORY_SOCIODEMOGRAPHIC && checkpoint == -1) {
            saveOralHealth();
        } else if (page == CATEGORY_ORAL_HEALTH && checkpoint == -1) {
            saveFamilyCohesion();
        } else if (page == END_PAGE) {
            if (checkpoint == -1) saveSociodemographic();
            else
                switch (checkpoint) {
                    case FAMILY_COHESION:
                        saveFamilyCohesion();
                        break;
                    case SOCIODEMOGRAPHICS:
                        saveSociodemographic();
                        break;
                    case ORAL_HEALTH:
                        saveOralHealth();
                        break;
                    default:
                        saveOralHealth();
                }
            sendQuestionnaireToServer();
        }
    }

    private void sendQuestionnaireToServer() {
        Log.w("AAA", "sendQuestionnaireToServer");
        ProgressDialog dialog = ProgressDialog.show(this, "Sincronização",
                "Aguarde alguns instantes...", true);
        dialog.show();
        Log.w("AAA", odontologicalQuestionnaire.toJson());
        if (updateType == null) {
            DisposableManager.add(haniotNetRepository
                    .saveOdontologicalQuestionnaire(patient.get_id(), odontologicalQuestionnaire)
                    .doAfterTerminate(() -> {
                    })
                    .subscribe(odontologicalQuestionnaire1 -> {
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
                        Log.w(LOG_TAG, "Error: " + throwable.getMessage());
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
                DisposableManager.add(haniotNetRepository
                        .updateOdontologicalQuestionnaire(patient.get_id(), idUpdate, updateType, resourceToUpdate)
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
        if (resourceToUpdate instanceof FamilyCohesionRecord) {
            Log.w("AAA", "FamilyCohesionRecordOB " + ((FamilyCohesionRecord) resourceToUpdate).toJson());
        } else if (resourceToUpdate instanceof OralHealthRecord) {
            Log.w("AAA", "OralHealthRecordOB " + ((OralHealthRecord) resourceToUpdate).toJson());
        } else if (resourceToUpdate instanceof SociodemographicRecord) {
            Log.w("AAA", "SociodemographicRecordOB " + ((SociodemographicRecord) resourceToUpdate).toJson());
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
            case 1:
                familyCohesionRecord.setFamilyMutualAidFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(1, indexValue);
                break;
            case 2:
                familyCohesionRecord.setFriendshipApprovalFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(2, indexValue);
                break;
            case 3:
                familyCohesionRecord.setFamilyOnlyTaskFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(3, indexValue);
                break;
            case 4:
                familyCohesionRecord.setFamilyOnlyPreferenceFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(4, indexValue);
                break;
            case 5:
                familyCohesionRecord.setFreeTimeTogetherFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(5, indexValue);
                break;
            case 6:
                familyCohesionRecord.setFamilyProximityPerceptionFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(6, indexValue);
                break;
            case 7:
                familyCohesionRecord.setAllFamilyTasksFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(7, indexValue);
                break;
            case 8:
                familyCohesionRecord.setFamilyTasksOpportunityFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(8, indexValue);
                break;
            case 9:
                familyCohesionRecord.setFamilyDecisionSupportFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(9, indexValue);
                break;
            case 10:
                familyCohesionRecord.setFamilyUnionRelevanceFreq(FrequencyAnswersType.Frequency.getString(indexValue));
                setPoint(10, indexValue);
                break;
            case 11:
                sociodemographicRecord.setColorRace(SociodemographicType.ColorRace.getString(indexValue));
                break;
            case 12:
                sociodemographicRecord.setMotherScholarity(SociodemographicType.MotherScholarity.getString(indexValue));
                break;
            case 14:
                oralHealthRecord.setTeethBrushingFreq(ToothLesionType.TeethBrushingFreq.getString(indexValue));
                break;
            case 15:
                if (indexValue == 0) {
                    ToothLesion toothLesion = new ToothLesion();
                    toothLesion.setToothType(ToothLesionType.ToothType.DECIDUOUS_TOOTH);
                    toothLesion.setLesionType(ToothLesionType.LesionType.CAVITATED_LESION);
                    toothLesions.add(toothLesion);
                }
                break;
            case 16:
                if (indexValue == 0) {
                    ToothLesion toothLesion = new ToothLesion();
                    toothLesion.setToothType(ToothLesionType.ToothType.PERMANENT_TOOTH);
                    toothLesion.setLesionType(ToothLesionType.LesionType.CAVITATED_LESION);
                    toothLesions.add(toothLesion);
                }
                break;
            case 17:
                if (indexValue == 0) {
                    ToothLesion toothLesion = new ToothLesion();
                    toothLesion.setToothType(ToothLesionType.ToothType.DECIDUOUS_TOOTH);
                    toothLesion.setLesionType(ToothLesionType.LesionType.WHITE_SPOT_LESION);
                    toothLesions.add(toothLesion);
                }
                break;
            case 18:
                if (indexValue == 0) {
                    ToothLesion toothLesion = new ToothLesion();
                    toothLesion.setToothType(ToothLesionType.ToothType.PERMANENT_TOOTH);
                    toothLesion.setLesionType(ToothLesionType.LesionType.WHITE_SPOT_LESION);
                    toothLesions.add(toothLesion);
                }
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

        if (page == 13) {
            sociodemographicRecord.setPeopleInHome(Integer.valueOf(value));
        }
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