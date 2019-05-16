package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import br.edu.uepb.nutes.haniot.adapter.EvaluationExpandableAdapter;
import br.edu.uepb.nutes.haniot.data.model.Evaluation;
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
import retrofit2.HttpException;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.*;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.*;

public class EvaluationActivity extends AppCompatActivity implements EvaluationExpandableAdapter.OnClick {

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

    EvaluationExpandableAdapter evaluationAdapter;
    private List<GroupItemEvaluation> groupItemEvaluations;
    private AppPreferencesHelper helper;
    private Patient patient;
    HaniotNetRepository haniotNetRepository;
    private String typeEvaluation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);
        initResources();

        typeEvaluation = "dentrist";
        initViews();
        loading();

    }

    /**
     * Initialize Evaluation recyclerview.
     */
    private void initRecyclerView() {
        evaluationAdapter = new EvaluationExpandableAdapter(groupItemEvaluations, this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluationAdapter.setListener(this);
        evaluation.setAdapter(evaluationAdapter);
        evaluationAdapter.expandAll();
    }

    /**
     * Initialize views.
     */
    private void initViews() {

        String nameType = "";
        if (typeEvaluation.equals("dentrist")) nameType = "Odontológica";
        else if (typeEvaluation.equals("nutrition")) nameType = "Nutricional";
        toolbar.setTitle("Gerar Avaliação " + nameType);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
     * Show recyclerview items in loading mode.
     */
    private void loading() {

        List<ItemEvaluation> itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_LOADING,
                getString(R.string.heart_rate), HEARTRATE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.heart_rate), itemsLoading, HEARTRATE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xblood_pressure, TYPE_LOADING,
                getString(R.string.blood_pressure), BLOOD_PRESSURE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.blood_pressure), itemsLoading, BLOOD_PRESSURE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xweight, TYPE_LOADING,
                getString(R.string.weight), BLOOD_PRESSURE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.weight), itemsLoading, WEIGHT));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_LOADING,
                getString(R.string.glucose), GLUCOSE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.glucose), itemsLoading, GLUCOSE));

//        if (typeEvaluation.equals("dentrist")) {
        if (true) {

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
        }

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Histórico de Saúde", itemsLoading, MEDICAL_RECORDS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING, "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Alimentares", itemsLoading, FEEDING_HABITS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Físicos", itemsLoading, PHYSICAL_ACTIVITY));

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

        if (groupItemEvaluation == null) {
            Log.i("AAAAA", "groupItemEvaluation não encontrado");
            return;
        }

        if (data.isEmpty() && !groupItemEvaluation.getItems().isEmpty()) {
            Log.i("AAAAA", "Vazio");
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_EMPTY_REQUIRED);

        } else {
            Log.i("AAAAA", "Não Vazio");
            ItemEvaluation itemEvaluation = groupItemEvaluation.getItems().get(0);
            groupItemEvaluation.getItems().clear();
            switch (type) {
                case SLEEP_HABITS:
                    for (SleepHabit sleepHabit : (List<SleepHabit>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setSleepHabit(sleepHabit);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case ORAL_HEALTH:
                    for (OralHealthRecord oralHealthRecord : (List<OralHealthRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setOralHealthRecord(oralHealthRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case FAMILY_COHESION:
                    for (FamilyCohesionRecord familyCohesionRecord : (List<FamilyCohesionRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setFamilyCohesionRecord(familyCohesionRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case SOCIODEMOGRAPHICS:
                    for (SociodemographicRecord sociodemographicRecord : (List<SociodemographicRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setSociodemographicRecord(sociodemographicRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case MEDICAL_RECORDS:
                    for (MedicalRecord medicalRecord : (List<MedicalRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setMedicalRecord(medicalRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case FEEDING_HABITS:
                    for (FeedingHabitsRecord feedingHabitsRecord : (List<FeedingHabitsRecord>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setFeedingHabitsRecord(feedingHabitsRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case PHYSICAL_ACTIVITY:
                    for (PhysicalActivityHabit physicalActivityHabit : (List<PhysicalActivityHabit>) data) {
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setPhysicalActivityHabit(physicalActivityHabit);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case GLUCOSE:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setValueMeasurement(String.valueOf(measurement.getValue()));
                        itemEvaluation.setUnitMeasurement(measurement.getUnit());
//                        itemEvaluation.setMeasuremetExtra(measurement.getMeal());
                        itemEvaluation.setDate(measurement.getTimestamp()); //TODO Mudar lógica no adapter
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case BLOOD_PRESSURE:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setValueMeasurement(measurement.getSystolic() + "/" + measurement.getDiastolic());
                        itemEvaluation.setUnitMeasurement(measurement.getUnit());
                        itemEvaluation.setDate(measurement.getTimestamp()); //TODO Mudar lógica no adapter
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
//                case TEMPERATURE:
//                case WAIST_CIRCUMFERENCE:
//                case HEIGHT:
//                case FAT:
                case WEIGHT:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setValueMeasurement(String.valueOf(measurement.getValue()));
                        itemEvaluation.setUnitMeasurement(measurement.getUnit());
                        itemEvaluation.setDate(measurement.getTimestamp()); //TODO Mudar lógica no adapter
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case HEARTRATE:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setDataset(measurement.getDataset());
//                        itemEvaluation.setValueMeasurement(measurement.getDataset().get(0).getValue() + " - "+measurement.getDataset().get(0).getTimestamp());
                        itemEvaluation.setUnitMeasurement(measurement.getUnit());
                        itemEvaluation.setDate(measurement.getTimestamp()); //TODO Mudar lógica no adapter
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
            }
            groupItemEvaluation.getItems().get(0).setChecked(true);
        }

        evaluationAdapter.notifyDataSetChanged();
    }

    /**
     * Show error.
     *
     * @param e
     */
    private void errorHandler(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);
            Log.i("AAA", httpEx.getMessage());
        }
        // message 500
    }

    /**
     * Download data evaluation from the server.
     */
    private void downloadData() {

//        if (typeEvaluation.equals("dentrist")) {
        if (true) {
            DisposableManager.add(haniotNetRepository
                    .getAllFamilyCohesion(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(familyCohesionRecords ->
                            prepareData(familyCohesionRecords, FAMILY_COHESION), this::errorHandler));

            DisposableManager.add(haniotNetRepository
                    .getAllSociodemographic(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(sociodemographicRecords ->
                            prepareData(sociodemographicRecords, SOCIODEMOGRAPHICS), this::errorHandler));

            DisposableManager.add(haniotNetRepository
                    .getAllOralHealth(helper.getLastPatient().get_id()
                            , 1, 20, "created_at")
                    .subscribe(oralHealthRecords ->
                            prepareData(oralHealthRecords, ORAL_HEALTH), this::errorHandler));
        }

        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(feedingHabitsRecords ->
                        prepareData(feedingHabitsRecords, FEEDING_HABITS), this::errorHandler));

        DisposableManager.add(haniotNetRepository
                .getAllMedicalRecord(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(medicalRecords ->
                        prepareData(medicalRecords, MEDICAL_RECORDS), this::errorHandler));

        DisposableManager.add(haniotNetRepository
                .getAllPhysicalActivity(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(physicalActivityHabits ->
                        prepareData(physicalActivityHabits, PHYSICAL_ACTIVITY), this::errorHandler));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sleepHabits ->
                        prepareData(sleepHabits, SLEEP_HABITS), this::errorHandler));

//        //TODO Mudar o dateStart e o dateEnd
//        DisposableManager.add(haniotNetRepository
//                .getAllMeasurementsByType(helper.getLastPatient().get_id()
//                        , "heart_rate", "created_at", "created_at", "created_at", 1, 1000)
//                .subscribe(measurements ->
//                        prepareData(measurements, HEARTRATE), this::errorHandler));
//
//        DisposableManager.add(haniotNetRepository
//                .getAllMeasurementsByType(helper.getLastPatient().get_id()
//                        , "blood_pressure", "created_at", "created_at", "created_at", 1, 1000)
//                .subscribe(measurements ->
//                        prepareData(measurements, BLOOD_PRESSURE), this::errorHandler));
//
//        DisposableManager.add(haniotNetRepository
//                .getAllMeasurementsByType(helper.getLastPatient().get_id()
//                        , "weight", "created_at", "created_at", "created_at", 1, 1000)
//                .subscribe(measurements ->
//                        prepareData(measurements, WEIGHT), this::errorHandler));
//
//        DisposableManager.add(haniotNetRepository
//                .getAllMeasurementsByType(helper.getLastPatient().get_id()
//                        , "glucose", "created_at", "created_at", "created_at", 1, 1000)
//                .subscribe(measurements ->
//                        prepareData(measurements, GLUCOSE), this::errorHandler));
        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().get_id()
                        , 1, 1000, "created_at")
                .subscribe(this::prepareMeasurements, this::errorHandler));
    }

    private void prepareMeasurements(List<Measurement> measurements) {

        List<Measurement> glucose = new ArrayList<>();
        List<Measurement> bloodPressure = new ArrayList<>();
        List<Measurement> temperature = new ArrayList<>();
        List<Measurement> fat = new ArrayList<>();
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
                case "body_temperature":
                    temperature.add(measurement);
                    break;
                case "fat":
                    fat.add(measurement);
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

        prepareData(weight, WEIGHT);
        prepareData(heartRate, HEARTRATE);
        prepareData(bloodPressure, BLOOD_PRESSURE);
        prepareData(glucose, GLUCOSE);
        prepareData(heartRate, HEARTRATE);
    }

    /**
     * Send evaluation for server.
     *
     * @param evaluation
     */
    private void sendEvaluation(Evaluation evaluation) {
        finish();
    }

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
        //TODO Criar lógica para ser específico
        groupItemEvaluations.clear();
        downloadData();
    }

    @Override
    public void onSelectClick(ItemEvaluation itemEvaluation) {
        itemEvaluation.setChecked(!itemEvaluation.isChecked());
    }
}
