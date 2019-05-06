package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_EMPTY;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.BLOOD_PRESSURE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.GLUCOSE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.HEARTRATE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.WEIGHT;

public class EvaluationActivity extends AppCompatActivity implements EvaluationExpandableAdapter.OnClick<ItemEvaluation> {

    @BindView(R.id.list_evaluation)
    RecyclerView evaluation;
    EvaluationExpandableAdapter evaluationAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gender_icon)
    ImageView gender;
    @BindView(R.id.send_evaluation)
    FloatingActionButton sendEvaluation;
    @BindView(R.id.message_patient)
    TextView messagePatient;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initResources();
        typeEvaluation = helper.getString("typeEvaluation");
        initViews();

        downloadData();
        initRecyclerView();
    }

    private void initRecyclerView() {
        evaluationAdapter = new EvaluationExpandableAdapter(groupItemEvaluations, this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluationAdapter.setListener(this);
        evaluation.setAdapter(evaluationAdapter);
    }

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

    private void initResources() {
        groupItemEvaluations = new ArrayList<>();
        helper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = helper.getLastPatient();

        sendEvaluation.setOnClickListener(v -> {
        });
    }

    private void prepareHeartRateMeasurement(List<Measurement> measurementsList) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (measurementsList.isEmpty())
            itemEvaluations.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_EMPTY,
                    getString(R.string.heart_rate), HEARTRATE));
        else
            for (Measurement measurement : measurementsList)
                itemEvaluations.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_MEASUREMENT,
                        getString(R.string.heart_rate), String.valueOf(measurement.getValue()),
                        String.valueOf(measurement.getUnit()), HEARTRATE));

        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.heart_rate), itemEvaluations));
    }

    private void prepareBloodPressureMeasurement(List<Measurement> measurementsList) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (measurementsList.isEmpty())
            itemEvaluations.add(new ItemEvaluation(R.drawable.xblood_pressure, TYPE_EMPTY,
                    getString(R.string.blood_pressure), BLOOD_PRESSURE));
        else
            for (Measurement measurement : measurementsList)
                itemEvaluations.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_MEASUREMENT,
                        getString(R.string.blood_pressure), String.valueOf(measurement.getValue()),
                        String.valueOf(measurement.getUnit()), BLOOD_PRESSURE));

        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.blood_pressure), itemEvaluations));
    }

    private void prepareWeightMeasurement(List<Measurement> measurementsList) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (measurementsList.isEmpty())
            itemEvaluations.add(new ItemEvaluation(R.drawable.xweight, TYPE_EMPTY,
                    getString(R.string.blood_pressure), BLOOD_PRESSURE));
        else
            for (Measurement measurement : measurementsList)
                itemEvaluations.add(new ItemEvaluation(R.drawable.xweight, TYPE_MEASUREMENT,
                        getString(R.string.weight), String.valueOf(measurement.getValue()),
                        String.valueOf(measurement.getUnit()), WEIGHT));

        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.weight), itemEvaluations));
    }

    private void prepareGlucoseMeasurement(List<Measurement> measurementsList) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (measurementsList.isEmpty())
            itemEvaluations.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_EMPTY,
                    getString(R.string.glucose), GLUCOSE));
        else
            for (Measurement measurement : measurementsList)
                itemEvaluations.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_MEASUREMENT,
                        getString(R.string.glucose), String.valueOf(measurement.getValue()),
                        String.valueOf(measurement.getUnit()), GLUCOSE));

        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.glucose), itemEvaluations));
    }

    /////////////////////////////////

    private void downloadData() {
        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().getUserId()
                        , "heart_rate", "created_at", 1, 1000)
                .subscribe(this::prepareHeartRateMeasurement));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().getUserId()
                        , "blood_pressure", "created_at", 1, 1000)
                .subscribe(this::prepareBloodPressureMeasurement));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().getUserId()
                        , "weight", "created_at", 1, 1000)
                .subscribe(this::prepareWeightMeasurement));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().getUserId()
                        , "glucose", "created_at", 1, 1000)
                .subscribe(this::prepareGlucoseMeasurement));

        if (typeEvaluation.equals("dentrist")) {
            DisposableManager.add(haniotNetRepository
                    .getAllOralHealth(helper.getLastPatient().getUserId()
                            , "glucose", "created_at", 1, 1000)
                    .subscribe(this::prepareOralHealth));

            DisposableManager.add(haniotNetRepository
                    .getAllFamilyCohesion(helper.getLastPatient().getUserId()
                            , "glucose", "created_at", 1, 1000)
                    .subscribe(this::prepareFamilyCohesion));

            DisposableManager.add(haniotNetRepository
                    .getAllSociodemographic(helper.getLastPatient().getUserId()
                            , "glucose", "created_at", 1, 1000)
                    .subscribe(this::prepareSociodemographic));

        }

        DisposableManager.add(haniotNetRepository
                .getAllMedicalRecord(helper.getLastPatient().getUserId()
                        , "glucose", "created_at", 1, 1000)
                .subscribe(this::prepareMedicalRecords));

        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(helper.getLastPatient().getUserId()
                        , "glucose", "created_at", 1, 1000)
                .subscribe(this::prepareFeedingHabits));

        DisposableManager.add(haniotNetRepository
                .getAllPhysicalActivity(helper.getLastPatient().getUserId()
                        , "glucose", "created_at", 1, 1000)
                .subscribe(this::preparePhysicalHabits));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(helper.getLastPatient().getUserId()
                        , "glucose", "created_at", 1, 1000)
                .subscribe(this::prepareSleepHatits));

    }

    private void prepareOralHealth(List<OralHealthRecord> oralHealthRecords) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (oralHealthRecords.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Saúde Bucal", TypeEvaluation.ORAL_HEALTH));
        } else {

            for (OralHealthRecord oralHealthRecord : oralHealthRecords) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Coesão Familiar", "Respostas:  ", oralHealthRecord.getCreatedAt(),
                        TypeEvaluation.ORAL_HEALTH);
                itemEvaluation.setOralHealthRecord(oralHealthRecord);
                itemEvaluations.add(itemEvaluation);
            }
        }

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Saúde Bucal", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    private void prepareFamilyCohesion(List<FamilyCohesionRecord> cohesionFamily) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (cohesionFamily.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Coesão Familiar", TypeEvaluation.FAMILY_COHESION));
        } else {
            for (FamilyCohesionRecord familyCohesionRecord : cohesionFamily) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Coesão Familiar", "Respostas:  ", familyCohesionRecord.getCreatedAt(),
                        TypeEvaluation.FAMILY_COHESION);
                itemEvaluation.setFamilyCohesionRecord(familyCohesionRecord);
                itemEvaluations.add(itemEvaluation);
            }
        }

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Coesão Familiar", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    private void prepareMedicalRecords(List<MedicalRecord> medicalRecords) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (medicalRecords.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
        } else {
            for (MedicalRecord medicalRecord : medicalRecords) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Histórico de Saúde", "Respostas:  ", medicalRecord.getCreatedAt(),
                        TypeEvaluation.FAMILY_COHESION);
                itemEvaluation.setMedicalRecord(medicalRecord);
                itemEvaluations.add(itemEvaluation);
            }
        }

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Histórico de Saúde", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    private void prepareFeedingHabits(List<FeedingHabitsRecord> feedingHabitsRecords) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (feedingHabitsRecords.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
        } else {
            for (FeedingHabitsRecord feedingHabitsRecord : feedingHabitsRecords) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Histórico de Saúde", "Respostas:  ", feedingHabitsRecord.getCreatedAt(),
                        TypeEvaluation.FAMILY_COHESION);
                itemEvaluation.setFeedingHabitsRecord(feedingHabitsRecord);
                itemEvaluations.add(itemEvaluation);
            }
        }
        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Hábitos Alimentares", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    private void preparePhysicalHabits(List<PhysicalActivityHabit> physicalActivityHabits) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (physicalActivityHabits.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
        } else {
            for (PhysicalActivityHabit physicalActivityHabit : physicalActivityHabits) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Histórico de Saúde", "Respostas:  ", physicalActivityHabit.getCreatedAt(),
                        TypeEvaluation.PHYSICAL_ACTIVITY);
                itemEvaluation.setPhysicalActivityHabit(physicalActivityHabit);
                itemEvaluations.add(itemEvaluation);
            }
        }
        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Hábitos Físicos", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    private void prepareSociodemographic(List<SociodemographicRecord> sociodemographicRecords) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (sociodemographicRecords.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Hábitos Físicos", TypeEvaluation.SOCIODEMOGRAPHICS));
        } else {
            for (SociodemographicRecord sociodemographicRecord : sociodemographicRecords) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Histórico de Saúde", "Respostas:  ", sociodemographicRecord.getCreatedAt(),
                        TypeEvaluation.SOCIODEMOGRAPHICS);
                itemEvaluation.setSociodemographicRecord(sociodemographicRecord);
                itemEvaluations.add(itemEvaluation);
            }
        }

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Hábitos Físicos", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    private void prepareSleepHatits(List<SleepHabit> sleepHabits) {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        if (sleepHabits.isEmpty()) {
            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                    "Hábitos Físicos", TypeEvaluation.SLEEP_HABITS));
        } else {
            for (SleepHabit sleepHabit : sleepHabits) {
                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
                        "Hábitos do Sono", "Respostas:  ", sleepHabit.getCreatedAt(),
                        TypeEvaluation.SLEEP_HABITS);
                itemEvaluation.setSleepHabit(sleepHabit);
                itemEvaluations.add(itemEvaluation);
            }
        }

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
                "Questionário Odontológico - Hábitos do Sono", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

    //////////////

    private void sendEvaluation(Evaluation evaluation) {
        finish();
    }

    @Override
    public void onItemClick(ItemEvaluation item) {
        item.setChecked(!item.isChecked());
        evaluationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLongItemClick(View v, ItemEvaluation item) {

    }

    @Override
    public void onMenuContextClick(View v, ItemEvaluation item) {

    }

    @Override
    public void onAddMeasurementClick(String name, int type) {
        Intent intent;
        switch (type) {
            case GLUCOSE:
                intent = new Intent(this, GlucoseActivity.class);
                break;
            case HEARTRATE:
                intent = new Intent(this, HeartRateActivity.class);
                break;
            case BLOOD_PRESSURE:
                intent = new Intent(this, BloodPressureHDPActivity.class);
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
}
