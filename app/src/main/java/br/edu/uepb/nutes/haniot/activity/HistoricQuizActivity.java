package br.edu.uepb.nutes.haniot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.EvaluationAdapter;
import br.edu.uepb.nutes.haniot.adapter.HistoricQuizAdapter;
import br.edu.uepb.nutes.haniot.data.model.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_EMPTY_REQUIRED;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_ERROR;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_LOADING;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ALL_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.BLOOD_PRESSURE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FAMILY_COHESION;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.GLUCOSE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.HEARTRATE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.HEIGHT;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ORAL_HEALTH;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SOCIODEMOGRAPHICS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.WAIST_CIRCUMFERENCE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.WEIGHT;

public class HistoricQuizActivity extends AppCompatActivity implements HistoricQuizAdapter.OnClick {

    @BindView(R.id.list_quiz)
    RecyclerView quizList;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.message_patient)
    TextView messagePatient;

    @BindView(R.id.gender_icon)
    ImageView gender;

    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private Patient patient;
    private List<GroupItemEvaluation> groupItemEvaluations;
    HistoricQuizAdapter historicQuizAdapter;

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
        messagePatient.setText(String.format(getResources().getString(R.string.quiz_historic_message),
                patient.getName()));
        if (patient.getGender().equals("female")) gender.setImageResource(R.drawable.x_girl);
        else gender.setImageResource(R.drawable.x_boy);
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
                .getAllMedicalRecord(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(medicalRecords ->
                                prepareData(medicalRecords, MEDICAL_RECORDS),
                        error -> {
                            Log.i("AAA", error.getMessage());
                            onDownloadError(MEDICAL_RECORDS);
                        }));

        DisposableManager.add(haniotNetRepository
                .getAllPhysicalActivity(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(physicalActivityHabits ->
                                prepareData(physicalActivityHabits, PHYSICAL_ACTIVITY),
                        type -> onDownloadError(PHYSICAL_ACTIVITY)));

        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(feedingHabitsRecords ->
                                prepareData(feedingHabitsRecords, FEEDING_HABITS),
                        type -> onDownloadError(FEEDING_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sleepHabits ->
                                prepareData(sleepHabits, SLEEP_HABITS),
                        type -> onDownloadError(SLEEP_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllOralHealth(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(oralHealthRecords ->
                                prepareData(oralHealthRecords, ORAL_HEALTH),
                        type -> onDownloadError(ORAL_HEALTH)));

        DisposableManager.add(haniotNetRepository
                .getAllFamilyCohesion(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(familyCohesionRecords ->
                                prepareData(familyCohesionRecords, FAMILY_COHESION),
                        type -> onDownloadError(FAMILY_COHESION)));

        DisposableManager.add(haniotNetRepository
                .getAllSociodemographic(appPreferencesHelper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sociodemographicRecords ->
                                prepareData(sociodemographicRecords, SOCIODEMOGRAPHICS),
                        type -> onDownloadError(SOCIODEMOGRAPHICS)));
    }

    /**
     * Prepare quizList data from the server.
     *
     * @param data
     * @param type
     */
    private void prepareData(List data, int type) {
        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);

        if (groupItemEvaluation == null) return;

        ItemEvaluation itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
        groupItemEvaluation.getItems().clear();
        if (type == SLEEP_HABITS) {
            for (SleepHabit sleepHabit : (List<SleepHabit>) data) {
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setSleepHabit(sleepHabit);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        } else if (type == MEDICAL_RECORDS) {
            for (MedicalRecord medicalRecord : (List<MedicalRecord>) data) {
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setMedicalRecord(medicalRecord);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        } else if (type == FEEDING_HABITS) {
            for (FeedingHabitsRecord feedingHabitsRecord : (List<FeedingHabitsRecord>) data) {
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setFeedingHabitsRecord(feedingHabitsRecord);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        } else if (type == PHYSICAL_ACTIVITY) {
            for (PhysicalActivityHabit physicalActivityHabit : (List<PhysicalActivityHabit>) data) {
                itemEvaluation.setChecked(true);
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setPhysicalActivityHabit(physicalActivityHabit);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        } else if (type == SOCIODEMOGRAPHICS) {
            for (SociodemographicRecord sociodemographicRecord : (List<SociodemographicRecord>) data) {
                itemEvaluation.setChecked(true);
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setSociodemographicRecord(sociodemographicRecord);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        } else if (type == FAMILY_COHESION) {
            for (FamilyCohesionRecord familyCohesionRecord : (List<FamilyCohesionRecord>) data) {
                itemEvaluation.setChecked(true);
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setFamilyCohesionRecord(familyCohesionRecord);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        } else if (type == ORAL_HEALTH) {
            for (OralHealthRecord oralHealthRecord : (List<OralHealthRecord>) data) {
                itemEvaluation.setChecked(true);
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setOralHealthRecord(oralHealthRecord);
                groupItemEvaluation.getItems().add(itemEvaluation);
                itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            }
        }
        historicQuizAdapter.notifyDataSetChanged();
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
        groupItemEvaluations.clear();
        List<ItemEvaluation> itemsLoading;

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Histórico de Saúde",
                itemsLoading, MEDICAL_RECORDS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Físicos",
                itemsLoading, PHYSICAL_ACTIVITY));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Alimentares",
                itemsLoading, FEEDING_HABITS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos do Sono",
                itemsLoading, SLEEP_HABITS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Sociodemográfico", SOCIODEMOGRAPHICS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Sociodemográfico",
                itemsLoading, SOCIODEMOGRAPHICS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Coesão Familiar", TypeEvaluation.FAMILY_COHESION));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Coesão Familiar",
                itemsLoading, FAMILY_COHESION));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Saúde Bucal", ORAL_HEALTH));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Saúde Bucal",
                itemsLoading, ORAL_HEALTH));

        initRecyclerView();
        downloadData();

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
