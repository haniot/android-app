package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.HistoricQuizAdapter;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_ERROR;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_LOADING;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ALL_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FAMILY_COHESION;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ORAL_HEALTH;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SOCIODEMOGRAPHICS;

public class HistoricQuizActivity extends AppCompatActivity implements HistoricQuizAdapter.OnClick {

    @BindView(R.id.list_quiz_nutrition)
    RecyclerView listNutritional;

    @BindView(R.id.list_quiz_odonto)
    RecyclerView listOdontological;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.message_patient)
    TextView messagePatient;

    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private Patient patient;
    private List<GroupItemEvaluation> groupItemNutritionEvaluations;
    private List<GroupItemEvaluation> groupItemOdontologicalEvaluations;
    HistoricQuizAdapter historicNutritionalAdapter;
    HistoricQuizAdapter historicOdontologicalAdapter;

    //TODO TEMP
    NutritionalQuestionnaire nutritionalQuestionnaire;
    OdontologicalQuestionnaire odontologicalQuestionnaire;

    @BindView(R.id.loading_nutrition)
    ProgressBar loadingNutrition;

    @BindView(R.id.loading_odontological)
    ProgressBar loadingOdontological;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_quiz);
        ButterKnife.bind(this);
        initResources();
        initToolbar();
    }

    private void initResources() {
        groupItemNutritionEvaluations = new ArrayList<>();
        groupItemOdontologicalEvaluations = new ArrayList<>();
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();
    }


    /**
     * Initialize toolbar.
     */
    private void initToolbar() {
        toolbar.setTitle("Histórico de Questionário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareItems();
    }

    /**
     * Download data listNutritional from the server.
     */
    private void downloadData() {
        DisposableManager.add(haniotNetRepository
                .getAllNutritionalQuestionnaires(patient.get_id(), 1, 100, "created_at")
                .subscribe(nutritional -> {
                    setNutritionalGroups(nutritional);
                    loadingNutrition.setVisibility(View.GONE);
                }, throwable -> {
                }));

        DisposableManager.add(haniotNetRepository
                .getAllOdontologicalQuestionnaires(patient.get_id(), 1, 100, "created_at")
                .subscribe(odontological -> {
                    setOdontologicalGroups(odontological);
                    loadingOdontological.setVisibility(View.GONE);
                }, throwable -> {
                }));
    }

    /**
     * TODO TEMP
     * Download data listNutritional from the server.
     */
    private void downloadDataTemp() {
        DisposableManager.add(haniotNetRepository
                .getAllNutritionalQuestionnaires(patient.get_id(), 1, 100, "created_at")
                .subscribe(this::setNutritionalGroups, throwable -> onDownloadError(ALL_QUIZ)));

        DisposableManager.add(haniotNetRepository
                .getAllOdontologicalQuestionnaires(patient.get_id(), 1, 100, "created_at")
                .subscribe(this::setOdontologicalGroups, throwable -> onDownloadError(ALL_QUIZ)));

        //TODO TEMP Teste
        nutritionalQuestionnaire = new NutritionalQuestionnaire();
        nutritionalQuestionnaire.setCreatedAt(DateUtils.getCurrentDateTimeUTC());

        DisposableManager.add(haniotNetRepository
                .getAllMedicalRecord(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(medicalRecords -> {

//                            setItem(medicalRecords, MEDICAL_RECORDS);
                            if (!medicalRecords.isEmpty()) {
                                Log.w("BBB", "Chegou medicalrecords");
                                nutritionalQuestionnaire.setMedicalRecord(medicalRecords.get(0));
                                nutritionalQuestionnaire.setCreatedAt(medicalRecords.get(0).getCreatedAt());
                            }
                        },
                        error -> {
                            Log.i("AAA", error.getMessage());
                            onDownloadError(MEDICAL_RECORDS);
                        }));

        DisposableManager.add(haniotNetRepository
                .getAllPhysicalActivity(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(physicalActivityHabits -> {
                            Log.w("BBB", "Chegou physicalActivityHabits");
//                            setItem(physicalActivityHabits, PHYSICAL_ACTIVITY);
                            if (!physicalActivityHabits.isEmpty())
                                nutritionalQuestionnaire.setPhysicalActivityHabit(physicalActivityHabits.get(0));
                        },
                        type -> onDownloadError(PHYSICAL_ACTIVITY)));

        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(feedingHabitsRecords -> {
                            Log.w("BBB", "Chegou feedingHabitsRecords");
//                            setItem(feedingHabitsRecords, FEEDING_HABITS);
                            if (!feedingHabitsRecords.isEmpty())
                                nutritionalQuestionnaire.setFeedingHabitsRecord(feedingHabitsRecords.get(0));
                        },
                        type -> onDownloadError(FEEDING_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sleepHabits -> {
                            Log.w("BBB", "Chegou sleepHabits");
//                            setItem(sleepHabits, SLEEP_HABITS);
                            if (!sleepHabits.isEmpty())
                                nutritionalQuestionnaire.setSleepHabit(sleepHabits.get(0));
                        },
                        type -> onDownloadError(SLEEP_HABITS)));

        odontologicalQuestionnaire = new OdontologicalQuestionnaire();
        odontologicalQuestionnaire.setCreatedAt(DateUtils.getCurrentDateTimeUTC());

        DisposableManager.add(haniotNetRepository
                .getAllOralHealth(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(oralHealthRecords -> {
                            Log.w("BBB", "oralHealthRecords sleepHabits");

//                            setItem(oralHealthRecords, ORAL_HEALTH);
                            if (!oralHealthRecords.isEmpty()) {
                                odontologicalQuestionnaire.setOralHealthRecord(oralHealthRecords.get(0));
                                odontologicalQuestionnaire.setCreatedAt(oralHealthRecords.get(0).getCreatedAt());
                            }
                        },
                        type -> onDownloadError(ORAL_HEALTH)));

        DisposableManager.add(haniotNetRepository
                .getAllFamilyCohesion(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(familyCohesionRecords -> {

//                            setItem(familyCohesionRecords, FAMILY_COHESION);
                            if (!familyCohesionRecords.isEmpty())
                                odontologicalQuestionnaire.setFamilyCohesionRecord(familyCohesionRecords.get(0));
                        },
                        type -> onDownloadError(FAMILY_COHESION)));

        DisposableManager.add(haniotNetRepository
                .getAllSociodemographic(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sociodemographicRecords -> {

//                            setItem(sociodemographicRecords, SOCIODEMOGRAPHICS);
                            if (!sociodemographicRecords.isEmpty())
                                odontologicalQuestionnaire.setSociodemographicRecord(sociodemographicRecords.get(0));
                        },
                        type -> onDownloadError(SOCIODEMOGRAPHICS)));

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            List<NutritionalQuestionnaire> nutritionalQuestionnaires = new ArrayList<>();
            nutritionalQuestionnaires.add(nutritionalQuestionnaire);
            nutritionalQuestionnaires.add(nutritionalQuestionnaire);
            nutritionalQuestionnaires.add(nutritionalQuestionnaire);
            setNutritionalGroups(nutritionalQuestionnaires);
            loadingNutrition.setVisibility(View.GONE);

            List<OdontologicalQuestionnaire> odontologicalQuestionnaires = new ArrayList<>();
            odontologicalQuestionnaires.add(odontologicalQuestionnaire);
            odontologicalQuestionnaires.add(odontologicalQuestionnaire);
            odontologicalQuestionnaires.add(odontologicalQuestionnaire);
            setOdontologicalGroups(odontologicalQuestionnaires);
            loadingOdontological.setVisibility(View.GONE);
        }, 3500);
    }

    /**
     * Prepare listNutritional data from the server.
     */
    private void setNutritionalItem(NutritionalQuestionnaire nutritionalQuestionnaire) {
        Log.w("BBBB", "setItem");
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setSleepHabit(nutritionalQuestionnaire.getSleepHabit());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setMedicalRecord(nutritionalQuestionnaire.getMedicalRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setFeedingHabitsRecord(nutritionalQuestionnaire.getFeedingHabitsRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setPhysicalActivityHabit(nutritionalQuestionnaire.getPhysicalActivityHabit());
        itemEvaluations.add(itemEvaluation);

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(DateUtils.convertDateTimeUTCToLocale(nutritionalQuestionnaire.getCreatedAt(), getString(R.string.datetime_format)),
                itemEvaluations, 1000);

        groupItemNutritionEvaluations.add(groupItemEvaluation);

    }


    /**
     * Prepare listNutritional data from the server.
     */
    private void setOdontologicalItem(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        Log.w("BBBB", "setItem");
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Sociodemográfico", SOCIODEMOGRAPHICS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setSociodemographicRecord(odontologicalQuestionnaire.getSociodemographicRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Coesão Familiar", FAMILY_COHESION);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setFamilyCohesionRecord(odontologicalQuestionnaire.getFamilyCohesionRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Saúde Bucal", ORAL_HEALTH);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setOralHealthRecord(odontologicalQuestionnaire.getOralHealthRecord());
        itemEvaluations.add(itemEvaluation);


        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(DateUtils.convertDateTimeUTCToLocale(odontologicalQuestionnaire.getCreatedAt(), getString(R.string.datetime_format)),
                itemEvaluations, 1000);

        groupItemOdontologicalEvaluations.add(groupItemEvaluation);

    }

    private void setNutritionalGroups(List<NutritionalQuestionnaire> nutritionalQuestionnaires) {

        for (NutritionalQuestionnaire nutritionalQuestionnaire : nutritionalQuestionnaires) {
            setNutritionalItem(nutritionalQuestionnaire);
        }
        initRecyclerView();
    }

    private void setOdontologicalGroups(List<OdontologicalQuestionnaire> odontologicalQuestionnaires) {

        for (OdontologicalQuestionnaire odontologicalQuestionnaire : odontologicalQuestionnaires) {
            setOdontologicalItem(odontologicalQuestionnaire);
        }
        initRecyclerView();
    }

    /**
     * Show error.
     *
     * @param
     */
    private void onDownloadError(int type) {
        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
        if (groupItemEvaluation == null) return;
        groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_ERROR);
    }

    /**
     * Get get listNutritional group object by type.
     *
     * @param type
     * @return
     */
    private GroupItemEvaluation getEvaluationGroupByType(int type) {
        for (GroupItemEvaluation groupItemEvaluation : groupItemNutritionEvaluations) {
            if (groupItemEvaluation.getType() == type) {
                return groupItemEvaluation;
            }
        }
        return null;
    }

    /**
     * Show recyclerview items in prepareItems mode.
     */
    private void prepareItems() {
        groupItemOdontologicalEvaluations.clear();
        groupItemNutritionEvaluations.clear();
//        downloadDataTemp();
        downloadData();
    }

    /**
     * Initialize NutritionalEvaluation recyclerview.
     */
    private void initRecyclerView() {
        historicNutritionalAdapter = new HistoricQuizAdapter(groupItemNutritionEvaluations, this);
        historicOdontologicalAdapter = new HistoricQuizAdapter(groupItemOdontologicalEvaluations, this);
        listNutritional.setLayoutManager(new LinearLayoutManager(this));
        listOdontological.setLayoutManager(new LinearLayoutManager(this));

        historicNutritionalAdapter.setListener(this);
        historicOdontologicalAdapter.setListener(this);
        listNutritional.setAdapter(historicNutritionalAdapter);
        listOdontological.setAdapter(historicOdontologicalAdapter);
        historicNutritionalAdapter.expandAll();
        historicOdontologicalAdapter.expandAll();
    }

    @Override
    public void onAddItemClick(String name, int type) {
        Intent intent = null;

        switch (type) {
            case MEDICAL_RECORDS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", MEDICAL_RECORDS);
                break;
            case SLEEP_HABITS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", SLEEP_HABITS);
                break;
            case FEEDING_HABITS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", FEEDING_HABITS);
                break;
            case PHYSICAL_ACTIVITY:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", PHYSICAL_ACTIVITY);
                break;
            case FAMILY_COHESION:
                intent = new Intent(this, QuizOdontologyActivity.class);
                intent.putExtra("checkpoint", FAMILY_COHESION);
                break;
            case ORAL_HEALTH:
                intent = new Intent(this, QuizOdontologyActivity.class);
                intent.putExtra("checkpoint", ORAL_HEALTH);
                break;
            case SOCIODEMOGRAPHICS:
                intent = new Intent(this, QuizOdontologyActivity.class);
                intent.putExtra("checkpoint", SOCIODEMOGRAPHICS);
                break;
        }
        if (intent != null) startActivity(intent);
    }

    @Override
    public void onSelectClick(ItemEvaluation itemEvaluation) {

    }
}
