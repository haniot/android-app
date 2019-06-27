package br.edu.uepb.nutes.haniot.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_ERROR;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_LOADING;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;

public class HistoricQuizActivity extends AppCompatActivity implements HistoricQuizAdapter.OnClick {

    @BindView(R.id.list_quiz)
    RecyclerView quizList;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.message_patient)
    TextView messagePatient;

    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private Patient patient;
    private List<GroupItemEvaluation> groupItemEvaluations;
    HistoricQuizAdapter historicQuizAdapter;
    NutritionalQuestionnaire nutritionalQuestionnaire;
    OdontologicalQuestionnaire odontologicalQuestionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_quiz);
        ButterKnife.bind(this);
        initResources();
        initToolbar();
    }

    private void initResources() {
        groupItemEvaluations = new ArrayList<>();
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
     * Download data quizList from the server.
     */
    private void downloadData() {
        DisposableManager.add(haniotNetRepository
                .getAllNutritionalQuestionnaires(patient.get_id(), 1, 100, "created_at")
                .subscribe(nutritionalQuestionnaires -> {
                    prepareData(nutritionalQuestionnaire);
                }, throwable -> {
                }));
    }

    /**
     * Download data quizList from the server.
     */
    private void downloadDataTemp() {
        //TODO TEMP Teste
        nutritionalQuestionnaire = new NutritionalQuestionnaire();

        DisposableManager.add(haniotNetRepository
                .getAllMedicalRecord(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(medicalRecords -> {

//                            prepareData(medicalRecords, MEDICAL_RECORDS);
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
//                            prepareData(physicalActivityHabits, PHYSICAL_ACTIVITY);
                            if (!physicalActivityHabits.isEmpty())
                                nutritionalQuestionnaire.setPhysicalActivityHabit(physicalActivityHabits.get(0));
                        },
                        type -> onDownloadError(PHYSICAL_ACTIVITY)));

        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(feedingHabitsRecords -> {
                            Log.w("BBB", "Chegou feedingHabitsRecords");
//                            prepareData(feedingHabitsRecords, FEEDING_HABITS);
                            if (!feedingHabitsRecords.isEmpty())
                                nutritionalQuestionnaire.setFeedingHabitsRecord(feedingHabitsRecords.get(0));
                        },
                        type -> onDownloadError(FEEDING_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sleepHabits -> {
                            Log.w("BBB", "Chegou sleepHabits");
//                            prepareData(sleepHabits, SLEEP_HABITS);
                            if (!sleepHabits.isEmpty())
                                nutritionalQuestionnaire.setSleepHabit(sleepHabits.get(0));
                        },
                        type -> onDownloadError(SLEEP_HABITS)));
//
//        odontologicalQuestionnaire = new OdontologicalQuestionnaire();
//        DisposableManager.add(haniotNetRepository
//                .getAllOralHealth(appPreferencesHelper.getLastPatient().get_id()
//                        , 1, 20, "created_at")
//                .subscribe(oralHealthRecords -> {
//                            Log.w("BBB", "oralHealthRecords sleepHabits");
//
////                            prepareData(oralHealthRecords, ORAL_HEALTH);
//                            if (!oralHealthRecords.isEmpty()) {
//                                odontologicalQuestionnaire.setOralHealthRecord(oralHealthRecords.get(0));
//                                odontologicalQuestionnaire.setCreatedAt(oralHealthRecords.get(0).getCreatedAt());
//                            }
//                        },
//                        type -> onDownloadError(ORAL_HEALTH)));
//
//        DisposableManager.add(haniotNetRepository
//                .getAllFamilyCohesion(appPreferencesHelper.getLastPatient().get_id()
//                        , 1, 20, "created_at")
//                .subscribe(familyCohesionRecords -> {
//
////                            prepareData(familyCohesionRecords, FAMILY_COHESION);
//                            if (!familyCohesionRecords.isEmpty())
//                                odontologicalQuestionnaire.setFamilyCohesionRecord(familyCohesionRecords.get(0));
//                        },
//                        type -> onDownloadError(FAMILY_COHESION)));
//
//        DisposableManager.add(haniotNetRepository
//                .getAllSociodemographic(appPreferencesHelper.getLastPatient().get_id()
//                        , 1, 20, "created_at")
//                .subscribe(sociodemographicRecords -> {
//
////                            prepareData(sociodemographicRecords, SOCIODEMOGRAPHICS);
//                            if (!sociodemographicRecords.isEmpty())
//                                odontologicalQuestionnaire.setSociodemographicRecord(sociodemographicRecords.get(0));
//                        },
//                        type -> onDownloadError(SOCIODEMOGRAPHICS)));

        final Handler handler = new Handler();
        handler.postDelayed(() -> prepareData(nutritionalQuestionnaire), 5500);

    }

    /**
     * Prepare quizList data from the server.
     */
    private void prepareData(NutritionalQuestionnaire nutritionalQuestionnaire) {
        Log.w("BBBB", "prepareData");
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

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation("19/06/2019 - 13:00",
                itemEvaluations, 1000);

        groupItemEvaluations.add(groupItemEvaluation);
//        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
//
//        itemEvaluation.setTypeHeader(TYPE_QUIZ);
//        itemEvaluation.setSociodemographicRecord(nutritionalQuestionnaire.getMedicalRecord());
//        groupItemEvaluation.getItems().add(itemEvaluation);
//        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
//
//        itemEvaluation.setTypeHeader(TYPE_QUIZ);
//        itemEvaluation.setFamilyCohesionRecord(familyCohesionRecord);
//        groupItemEvaluation.getItems().add(itemEvaluation);
//        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
//
//        itemEvaluation.setTypeHeader(TYPE_QUIZ);
//        itemEvaluation.setOralHealthRecord(oralHealthRecord);
//        groupItemEvaluation.getItems().add(itemEvaluation);
//        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();

//        historicQuizAdapter.notifyDataSetChanged();
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
     * Get get quizList group object by type.
     *
     * @param type
     * @return
     */
    private GroupItemEvaluation getEvaluationGroupByType(int type) {
        for (GroupItemEvaluation groupItemEvaluation : groupItemEvaluations) {
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
//        List<ItemEvaluation> itemsLoading;
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
//        groupItemEvaluations.add(new GroupItemEvaluation("19/06/2019 - 13:00",
//                itemsLoading, MEDICAL_RECORDS));
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
//        groupItemEvaluations.add(new GroupItemEvaluation("18/06/2019 - 13:00",
//                itemsLoading, PHYSICAL_ACTIVITY));
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
//        groupItemEvaluations.add(new GroupItemEvaluation("17/06/2019 - 13:00",
//                itemsLoading, FEEDING_HABITS));
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS));
//        groupItemEvaluations.add(new GroupItemEvaluation("16/06/2019 - 13:00",
//                itemsLoading, SLEEP_HABITS));
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Sociodemográfico", SOCIODEMOGRAPHICS));
//        groupItemEvaluations.add(new GroupItemEvaluation("15/06/2019 - 13:00",
//                itemsLoading, SOCIODEMOGRAPHICS));
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Coesão Familiar", TypeEvaluation.FAMILY_COHESION));
//        groupItemEvaluations.add(new GroupItemEvaluation("14/06/2019 - 13:00",
//                itemsLoading, FAMILY_COHESION));
//
//        itemsLoading = new ArrayList<>();
//        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
//                "Saúde Bucal", ORAL_HEALTH));
//        groupItemEvaluations.add(new GroupItemEvaluation("13/06/2019 - 13:00",
//                itemsLoading, ORAL_HEALTH));

        downloadDataTemp();

    }

    /**
     * Initialize NutritionalEvaluation recyclerview.
     */
    private void initRecyclerView() {
        historicQuizAdapter = new HistoricQuizAdapter(groupItemEvaluations, this);
        quizList.setLayoutManager(new LinearLayoutManager(this));
        historicQuizAdapter.setListener(this);
        quizList.setAdapter(historicQuizAdapter);
        historicQuizAdapter.expandAll();
    }

    @Override
    public void onAddItemClick(String name, int type) {

    }

    @Override
    public void onSelectClick(ItemEvaluation itemEvaluation, boolean selected) {

    }
}
