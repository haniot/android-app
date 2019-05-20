package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.EvaluationExpandableAdapter;
import br.edu.uepb.nutes.haniot.data.model.DentristEvaluation;
import br.edu.uepb.nutes.haniot.data.model.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
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
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.*;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.*;

public class EvaluationActivity extends AppCompatActivity implements EvaluationExpandableAdapter.OnClick {

    final private String NUTRITION = "nutrition";
    final private String DENTRIST = "dentrist";

    @BindView(R.id.list_evaluation)
    RecyclerView evaluation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gender_icon)
    ImageView gender;
    @BindView(R.id.send_evaluation)
    FloatingActionButton sendEvaluation;
    @BindView(R.id.message_patient)
    TextView messagePatient;
    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;
    @BindView(R.id.message_error)
    TextView messageError;


    EvaluationExpandableAdapter evaluationAdapter;
    private List<GroupItemEvaluation> groupItemEvaluations;
    private AppPreferencesHelper helper;
    private Patient patient;
    private HaniotNetRepository haniotNetRepository;
    private String typeEvaluation;
    private NutritionalEvaluation nutritionalEvaluation;
    private DentristEvaluation dentristEvaluation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);
        initResources();

        typeEvaluation = getIntent().getStringExtra("type");
        nutritionalEvaluation = new NutritionalEvaluation();
        dentristEvaluation = new DentristEvaluation();
        initToolbar();
        prepareItems();
        showMessage(-1);
        sendEvaluation.setOnClickListener(v -> {
            sendEvaluation();
        });

    }

    /**
     * Initialize NutritionalEvaluation recyclerview.
     */
    private void initRecyclerView() {
        evaluationAdapter = new EvaluationExpandableAdapter(groupItemEvaluations, this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluationAdapter.setListener(this);
        evaluation.setAdapter(evaluationAdapter);
        evaluationAdapter.expandAll();
    }

    /**
     * Initialize toolbar.
     */
    private void initToolbar() {
        String nameType = "";
        if (typeEvaluation.equals(DENTRIST)) nameType = "Odontológica";
        else if (typeEvaluation.equals(NUTRITION)) nameType = "Nutricional";
        toolbar.setTitle("Gerar Avaliação " + nameType);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messagePatient.setText(String.format(getResources().getString(R.string.evaluation_message),
                patient.getFirstName() + patient.getLastName(), nameType));
        if (patient.getGender().equals("female")) gender.setImageResource(R.drawable.x_girl);
        else gender.setImageResource(R.drawable.x_boy);
    }

    /**
     * Initialize resources.
     */
    private void initResources() {
        groupItemEvaluations = new ArrayList<>();
        helper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = helper.getLastPatient();
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    public void showMessage(@StringRes int str) {
        if (str == -1) {
            boxMessage.setVisibility(View.GONE);
            return;
        }

        String message = getResources().getString(str);
        messageError.setText(message);
        runOnUiThread(() -> {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            boxMessage.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Get get evaluation group object by type.
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

        List<ItemEvaluation> itemsLoading;

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xweight, TYPE_LOADING,
                getString(R.string.weight), WEIGHT));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.weight), itemsLoading, WEIGHT));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_LOADING,
                getString(R.string.glucose), GLUCOSE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.glucose), itemsLoading, GLUCOSE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.ic_height, TYPE_LOADING,
                getString(R.string.height), HEIGHT));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.height), itemsLoading, HEIGHT));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.ic_waist, TYPE_LOADING,
                getString(R.string.waits_circumference), WAIST_CIRCUMFERENCE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.waits_circumference), itemsLoading, WAIST_CIRCUMFERENCE));

        if (typeEvaluation.equals(DENTRIST)) {
            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                    "Saúde Bucal", TypeEvaluation.ORAL_HEALTH));
            groupItemEvaluations.add(new GroupItemEvaluation("Questionário Odontológico - Saúde Bucal", itemsLoading, ORAL_HEALTH));

            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                    "Coesão Familiar", TypeEvaluation.FAMILY_COHESION));
            groupItemEvaluations.add(new GroupItemEvaluation("Questionário Odontológico - Coesão Familiar", itemsLoading, FAMILY_COHESION));

            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                    "Sociodemográfico", TypeEvaluation.SOCIODEMOGRAPHICS));
            groupItemEvaluations.add(new GroupItemEvaluation("Questionário Odontológico - Sociodemográfico", itemsLoading, SOCIODEMOGRAPHICS));

        } else {

            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_LOADING,
                    getString(R.string.heart_rate), HEARTRATE));
            groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.heart_rate), itemsLoading, HEARTRATE));

            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.xblood_pressure, TYPE_LOADING,
                    getString(R.string.blood_pressure), BLOOD_PRESSURE));
            groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.blood_pressure), itemsLoading, BLOOD_PRESSURE));

            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                    "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
            groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Histórico de Saúde", itemsLoading, MEDICAL_RECORDS));

            itemsLoading = new ArrayList<>();
            itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                    "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
            groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Físicos", itemsLoading, PHYSICAL_ACTIVITY));

        }

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING, "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Alimentares", itemsLoading, FEEDING_HABITS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos do Sono", itemsLoading, SLEEP_HABITS));

        initRecyclerView();
        downloadData();

    }

    /**
     * Prepare evaluation data from the server.
     *
     * @param data
     * @param type
     */
    private void prepareData(List data, int type) {
        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);

        if (groupItemEvaluation == null) return;

        if (data.isEmpty() && !groupItemEvaluation.getItems().isEmpty())
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_EMPTY_REQUIRED);
        else {
            ItemEvaluation itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            groupItemEvaluation.getItems().clear();
            switch (type) {
                case SLEEP_HABITS:
                    for (SleepHabit sleepHabit : (List<SleepHabit>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setSleepHabit(sleepHabit);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                case ORAL_HEALTH:
                    for (OralHealthRecord oralHealthRecord : (List<OralHealthRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setOralHealthRecord(oralHealthRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                case FAMILY_COHESION:
                    for (FamilyCohesionRecord familyCohesionRecord : (List<FamilyCohesionRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setFamilyCohesionRecord(familyCohesionRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                case SOCIODEMOGRAPHICS:
                    for (SociodemographicRecord sociodemographicRecord : (List<SociodemographicRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setSociodemographicRecord(sociodemographicRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                case MEDICAL_RECORDS:
                    for (MedicalRecord medicalRecord : (List<MedicalRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setMedicalRecord(medicalRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                case FEEDING_HABITS:
                    for (FeedingHabitsRecord feedingHabitsRecord : (List<FeedingHabitsRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setFeedingHabitsRecord(feedingHabitsRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                case PHYSICAL_ACTIVITY:
                    for (PhysicalActivityHabit physicalActivityHabit : (List<PhysicalActivityHabit>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setPhysicalActivityHabit(physicalActivityHabit);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
                    break;
                default:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setMeasurement(measurement);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                    }
            }
        }
        evaluationAdapter.notifyDataSetChanged();
    }

    /**
     * Show error.
     *
     * @param
     */
    private void onDownloadError(int type) {
        if (type == ALL_MEASUREMENT) {
            getEvaluationGroupByType(WEIGHT).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(GLUCOSE).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(WAIST_CIRCUMFERENCE).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(HEIGHT).getItems().get(0).setTypeHeader(TYPE_ERROR);
            if (typeEvaluation.equals(NUTRITION)) {
                getEvaluationGroupByType(HEARTRATE).getItems().get(0).setTypeHeader(TYPE_ERROR);
                getEvaluationGroupByType(BLOOD_PRESSURE).getItems().get(0).setTypeHeader(TYPE_ERROR);
            }
        } else {
            GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
            if (groupItemEvaluation == null) return;
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_ERROR);
        }
    }

    /**
     * Download data evaluation from the server.
     */
    private void downloadData() {
        if (typeEvaluation.equals(DENTRIST)) {
            DisposableManager.add(haniotNetRepository
                    .getAllFamilyCohesion(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(familyCohesionRecords ->
                            prepareData(familyCohesionRecords, FAMILY_COHESION), type -> onDownloadError(FAMILY_COHESION)));

            DisposableManager.add(haniotNetRepository
                    .getAllSociodemographic(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(sociodemographicRecords ->
                            prepareData(sociodemographicRecords, SOCIODEMOGRAPHICS), type -> onDownloadError(SOCIODEMOGRAPHICS)));

            DisposableManager.add(haniotNetRepository
                    .getAllOralHealth(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(oralHealthRecords ->
                            prepareData(oralHealthRecords, ORAL_HEALTH), type -> onDownloadError(ORAL_HEALTH)));

        } else {
            DisposableManager.add(haniotNetRepository
                    .getAllMedicalRecord(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(medicalRecords ->
                            prepareData(medicalRecords, MEDICAL_RECORDS), type -> onDownloadError(MEDICAL_RECORDS)));

            DisposableManager.add(haniotNetRepository
                    .getAllPhysicalActivity(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(physicalActivityHabits ->
                            prepareData(physicalActivityHabits, PHYSICAL_ACTIVITY), type -> onDownloadError(PHYSICAL_ACTIVITY)));

        }
        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(feedingHabitsRecords ->
                        prepareData(feedingHabitsRecords, FEEDING_HABITS), type -> onDownloadError(FEEDING_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sleepHabits ->
                        prepareData(sleepHabits, SLEEP_HABITS), type -> onDownloadError(SLEEP_HABITS)));
        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().get_id()
                        , 1, 1000, "created_at")
                .subscribe(this::prepareMeasurements, type -> onDownloadError(ALL_MEASUREMENT)));
    }

    private void prepareMeasurements(List<Measurement> measurements) {
        List<Measurement> glucose = new ArrayList<>();
        List<Measurement> bloodPressure = new ArrayList<>();
        List<Measurement> heartRate = new ArrayList<>();
        List<Measurement> height = new ArrayList<>();
        List<Measurement> waistCircumference = new ArrayList<>();
        List<Measurement> weight = new ArrayList<>();

        for (Measurement measurement : measurements) {
            Log.i("AAA", measurement.getValue() + " - " + measurement.getType());
            switch (measurement.getType()) {
                case "blood_glucose":
                    glucose.add(measurement);
                    break;
                case "blood_pressure":
                    bloodPressure.add(measurement);
                    break;
                case "heart_rate":
                    heartRate.add(measurement);
                    break;
                case "height":
                    height.add(measurement);
                    break;
                case "waist_circumference":
                    waistCircumference.add(measurement);
                    break;
                case "weight":
                    weight.add(measurement);
                    break;
            }
        }

        if (typeEvaluation.equals(NUTRITION)) {
            prepareData(heartRate, HEARTRATE);
            prepareData(bloodPressure, BLOOD_PRESSURE);
        }
        prepareData(weight, WEIGHT);
        prepareData(glucose, GLUCOSE);
        prepareData(waistCircumference, WAIST_CIRCUMFERENCE);
        prepareData(height, HEIGHT);
    }

    /**
     * Send nutritionalEvaluation for server.
     */
    private void sendEvaluation() {
        Log.i("AAA", "Preparing evaluation...");
        if (typeEvaluation.equals(NUTRITION)) {
            boolean ok;
            ok = nutritionalEvaluation.getFeedingHabits() != null
                    && nutritionalEvaluation.getMedicalRecord() != null
                    && nutritionalEvaluation.getPhysicalActivityHabits() != null
                    && nutritionalEvaluation.getSleepHabits() != null;

            ok = hasMeasurement("heart_rate")
                    && hasMeasurement("blood_glucose")
                    && hasMeasurement("blood_pressure")
                    && hasMeasurement("weight")
                    && hasMeasurement("waist_circumference");
            if (ok) {
                DisposableManager.add(haniotNetRepository
                        .saveNutritionalEvaluation(nutritionalEvaluation)
                        .subscribe(nutritionalEvaluationResult -> {

                        }));
            } else {
                showMessage(R.string.evaluation_required_fields);
            }
        }
    }

    private boolean hasMeasurement(String type) {
        if (nutritionalEvaluation.getMeasurements() == null) return false;
        for (Measurement measurement : nutritionalEvaluation.getMeasurements())
            if (measurement.getType().equals(type)) return true;
        return false;
    }

//
//    private ItemEvaluation getItemChecked(GroupItemEvaluation groupItemEvaluation) {
//        for (ItemEvaluation itemEvaluation : groupItemEvaluation.getItems()) {
//            if (itemEvaluation.isChecked()) return itemEvaluation;
//        }
//        return null;
//    }
//
//    private List<ItemEvaluation> getMultiplesItemChecked(GroupItemEvaluation groupItemEvaluation) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//        for (ItemEvaluation itemEvaluation : groupItemEvaluation.getItems()) {
//            if (itemEvaluation.isChecked()) itemEvaluations.add(itemEvaluation);
//        }
//        return itemEvaluations;
//    }

    @Override
    public void onAddItemClick(String name, int type) {
        Intent intent;
        switch (type) {
            case GLUCOSE:
                intent = new Intent(this, GlucoseActivity.class);
                break;
            case HEARTRATE:
                intent = new Intent(this, HeartRateActivity.class);
                break;
            case WEIGHT:
                intent = new Intent(this, ScaleActivity.class);
                break;
            case TypeEvaluation
                    .ANTROPOMETRICHS:
                intent = new Intent(this, AddMeasurementActivity.class);
                AppPreferencesHelper.getInstance(this)
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.ANTHROPOMETRIC);
                break;
            case TypeEvaluation
                    .MEDICAL_RECORDS:
                intent = new Intent(this, HeartRateActivity.class);
                break;
            case TypeEvaluation
                    .ORAL_HEALTH:
                intent = new Intent(this, HeartRateActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

    @Override
    public void onRefreshClick(String name, int type) {
//        groupItemEvaluations.clear();
//        downloadData();
    }

    @Override
    public void onSelectClick(ItemEvaluation itemEvaluation, boolean selected) {
        Log.i("AAA", "Antes:" + nutritionalEvaluation.toString());
        if (itemEvaluation.getTypeHeader() == TYPE_MEASUREMENT) {
            if (selected) nutritionalEvaluation.addMeasuerement(itemEvaluation.getMeasurement());
            else nutritionalEvaluation.removeMeasuerement(itemEvaluation.getMeasurement());
        }
        switch (itemEvaluation.getTypeEvaluation()) {
            case SLEEP_HABITS:
                if (selected) nutritionalEvaluation.setSleepHabits(itemEvaluation.getSleepHabit());
                else nutritionalEvaluation.setSleepHabits(null);
                break;
            case MEDICAL_RECORDS:
                if (selected)
                    nutritionalEvaluation.setMedicalRecord(itemEvaluation.getMedicalRecord());
                else nutritionalEvaluation.setMedicalRecord(null);
                break;
            case SOCIODEMOGRAPHICS:
                if (selected)
                    dentristEvaluation.setSociodemographicRecord(itemEvaluation.getSociodemographicRecord());
                else dentristEvaluation.setSociodemographicRecord(null);
                break;
            case FAMILY_COHESION:
                if (selected)
                    dentristEvaluation.setFamilyCohesionRecord(itemEvaluation.getFamilyCohesionRecord());
                else dentristEvaluation.setFamilyCohesionRecord(null);
                break;
            case PHYSICAL_ACTIVITY:
                if (selected)
                    nutritionalEvaluation.setPhysicalActivityHabits(itemEvaluation.getPhysicalActivityHabit());
                else nutritionalEvaluation.setPhysicalActivityHabits(null);
                break;
            case FEEDING_HABITS:
                if (selected)
                    nutritionalEvaluation.setFeedingHabits(itemEvaluation.getFeedingHabitsRecord());
                else nutritionalEvaluation.setFeedingHabits(null);
                break;
            case ORAL_HEALTH:
                if (selected)
                    dentristEvaluation.setOralHealthRecord(itemEvaluation.getOralHealthRecord());
                else dentristEvaluation.setOralHealthRecord(null);
                break;
            default:
                break;
        }
        Log.i("AAA", "Depois:" + nutritionalEvaluation.toString());
    }
}
