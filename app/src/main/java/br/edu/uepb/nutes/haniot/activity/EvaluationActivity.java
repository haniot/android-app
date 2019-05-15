package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.*;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.*;

public class EvaluationActivity<T> extends AppCompatActivity implements EvaluationExpandableAdapter.OnClick {

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
//
//    GroupItemEvaluation groupItemEvaluationHeartRate;
//    GroupItemEvaluation groupItemEvaluationBloodPressure;
//    GroupItemEvaluation groupItemEvaluationWeight;
//    GroupItemEvaluation groupItemEvaluationGlucose;
//    GroupItemEvaluation groupItemEvaluationOralHealth;
//    GroupItemEvaluation groupItemEvaluationFamilyCohesion;
//    GroupItemEvaluation groupItemEvaluationMedicalRecords;
//    GroupItemEvaluation groupItemEvaluationFeedingsHabits;
//    GroupItemEvaluation groupItemEvaluationPhysicalHabits;
//    GroupItemEvaluation groupItemEvaluationSociodemographics;
//    GroupItemEvaluation groupItemEvaluationSleepHabits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);
        initResources();

        typeEvaluation = "dentrist";
        initViews();
        loading();

//        downloadData();
    }

    private void initRecyclerView() {
        evaluationAdapter = new EvaluationExpandableAdapter(groupItemEvaluations, this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluationAdapter.setListener(this);
        evaluation.setAdapter(evaluationAdapter);
        evaluationAdapter.expandAll();
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
            ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ, "Hábitos Alimentares", FEEDING_HABITS);

//// TESTE
////            ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.xcardiogram, TYPE_QUIZ,
////                    getString(R.string.heart_rate), String.valueOf("232"),
////                    String.valueOf("212"), HEARTRATE);
////            itemEvaluation.setValueMeasurement("92");
////            itemEvaluation.setUnitMeasurement("bpm");
////            itemEvaluation.setDate("22/05/2019");
////            itemEvaluation.setTime("03:34");
//            groupItemEvaluationSleepHabits.getItems().clear();
//            groupItemEvaluationSleepHabits.getItems().add(itemEvaluation);
//            evaluationAdapter.notifyDataSetChanged();
        });
    }

    private GroupItemEvaluation getGroupEvaluationByType(int type) {
        for (GroupItemEvaluation groupItemEvaluation : groupItemEvaluations) {
            if (groupItemEvaluation.getType() == type) {
                return groupItemEvaluation;
            }
        }
        return null;
    }

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
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.weight), itemsLoading, BLOOD_PRESSURE));

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

//
//    private void prepareHeartRateMeasurement(List<Measurement> measurementsList) {
//        List<ItemEvaluation> itemEvaluationsHeartRate = new ArrayList<>();
//        groupItemEvaluationHeartRate = new GroupItemEvaluation(getString(R.string.heart_rate), itemEvaluationsHeartRate);
//        if (measurementsList.isEmpty())
//            itemEvaluationsHeartRate.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_LOADING,
//                    getString(R.string.heart_rate), HEARTRATE));
//        else
//            for (Measurement measurement : measurementsList)
//                itemEvaluationsHeartRate.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_MEASUREMENT,
//                        getString(R.string.heart_rate), String.valueOf(measurement.getValue()),
//                        String.valueOf(measurement.getUnit()), HEARTRATE));
//        groupItemEvaluations.add(groupItemEvaluationHeartRate);
//    }
//
//    private void prepareBloodPressureMeasurement(List<Measurement> measurementsList) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (measurementsList.isEmpty())
//            itemEvaluations.add(new ItemEvaluation(R.drawable.xblood_pressure, TYPE_LOADING,
//                    getString(R.string.blood_pressure), BLOOD_PRESSURE));
//        else
//            for (Measurement measurement : measurementsList)
//                itemEvaluations.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_MEASUREMENT,
//                        getString(R.string.blood_pressure), String.valueOf(measurement.getValue()),
//                        String.valueOf(measurement.getUnit()), BLOOD_PRESSURE));
//
//        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.blood_pressure), itemEvaluations));
//    }
//
//    private void prepareWeightMeasurement(List<Measurement> measurementsList) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (measurementsList.isEmpty())
//            itemEvaluations.add(new ItemEvaluation(R.drawable.xweight, TYPE_LOADING,
//                    getString(R.string.blood_pressure), BLOOD_PRESSURE));
//        else
//            for (Measurement measurement : measurementsList)
//                itemEvaluations.add(new ItemEvaluation(R.drawable.xweight, TYPE_MEASUREMENT,
//                        getString(R.string.weight), String.valueOf(measurement.getValue()),
//                        String.valueOf(measurement.getUnit()), WEIGHT));
//
//        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.weight), itemEvaluations));
//    }
//
//    private void prepareGlucoseMeasurement(List<Measurement> measurementsList) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (measurementsList.isEmpty())
//            itemEvaluations.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_LOADING,
//                    getString(R.string.glucose), GLUCOSE));
//        else
//            for (Measurement measurement : measurementsList)
//                itemEvaluations.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_MEASUREMENT,
//                        getString(R.string.glucose), String.valueOf(measurement.getValue()),
//                        String.valueOf(measurement.getUnit()), GLUCOSE));
//
//        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.glucose), itemEvaluations));
//    }
//
//    private void prepareOralHealth(List<OralHealthRecord> oralHealthRecords) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (oralHealthRecords.isEmpty()) {
//            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                    "Saúde Bucal", TypeEvaluation.ORAL_HEALTH));
//        } else {
//
//            for (OralHealthRecord oralHealthRecord : oralHealthRecords) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Coesão Familiar", "Respostas:  ", oralHealthRecord.getCreatedAt(),
//                        TypeEvaluation.ORAL_HEALTH);
//                itemEvaluation.setOralHealthRecord(oralHealthRecord);
//                itemEvaluations.add(itemEvaluation);
//            }
//        }
//
//        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
//                "Questionário Odontológico - Saúde Bucal", itemEvaluations);
//        groupItemEvaluations.add(groupItemEvaluation);
//    }
//
//    private void prepareFamilyCohesion(List<FamilyCohesionRecord> cohesionFamily) {
//        groupItemEvaluationFamilyCohesion.getItems().clear();
//
//        if (cohesionFamily.isEmpty()) {
//            groupItemEvaluationFamilyCohesion.getItems().add(new ItemEvaluation(R.drawable.action_quiz, TYPE_EMPTY,
//                    "Coesão Familiar", TypeEvaluation.FAMILY_COHESION));
//        } else {
//            for (FamilyCohesionRecord familyCohesionRecord : cohesionFamily) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Coesão Familiar", "Respostas:  ", familyCohesionRecord.getCreatedAt(),
//                        TypeEvaluation.FAMILY_COHESION);
//                itemEvaluation.setFamilyCohesionRecord(familyCohesionRecord);
//                groupItemEvaluationFamilyCohesion.getItems().add(itemEvaluation);
//            }
//        }
//        evaluationAdapter.notifyDataSetChanged();
//    }
//
//    private void prepareMedicalRecords(List<MedicalRecord> medicalRecords) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (medicalRecords.isEmpty()) {
//            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                    "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
//        } else {
//            for (MedicalRecord medicalRecord : medicalRecords) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Histórico de Saúde", "Respostas:  ", medicalRecord.getCreatedAt(),
//                        TypeEvaluation.FAMILY_COHESION);
//                itemEvaluation.setMedicalRecord(medicalRecord);
//                itemEvaluations.add(itemEvaluation);
//            }
//        }
//
//        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
//                "Questionário Odontológico - Histórico de Saúde", itemEvaluations);
//        groupItemEvaluations.add(groupItemEvaluation);
//    }
//
//    private void prepareFeedingHabits(List<FeedingHabitsRecord> feedingHabitsRecords) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (feedingHabitsRecords.isEmpty()) {
//            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                    "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
//        } else {
//            for (FeedingHabitsRecord feedingHabitsRecord : feedingHabitsRecords) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Histórico de Saúde", "Respostas:  ", feedingHabitsRecord.getCreatedAt(),
//                        TypeEvaluation.FAMILY_COHESION);
//                itemEvaluation.setFeedingHabitsRecord(feedingHabitsRecord);
//                itemEvaluations.add(itemEvaluation);
//            }
//        }
//        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
//                "Questionário Odontológico - Hábitos Alimentares", itemEvaluations);
//        groupItemEvaluations.add(groupItemEvaluation);
//    }
//
//    private void preparePhysicalHabits(List<PhysicalActivityHabit> physicalActivityHabits) {
//        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
//
//        if (physicalActivityHabits.isEmpty()) {
//            itemEvaluations.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                    "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
//        } else {
//            for (PhysicalActivityHabit physicalActivityHabit : physicalActivityHabits) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Histórico de Saúde", "Respostas:  ", physicalActivityHabit.getCreatedAt(),
//                        TypeEvaluation.PHYSICAL_ACTIVITY);
//                itemEvaluation.setPhysicalActivityHabit(physicalActivityHabit);
//                itemEvaluations.add(itemEvaluation);
//            }
//        }
//        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(
//                "Questionário Odontológico - Hábitos Físicos", itemEvaluations);
//        groupItemEvaluations.add(groupItemEvaluation);
//    }
//
//    private void prepareSociodemographic(List<SociodemographicRecord> sociodemographicRecords) {
//        groupItemEvaluationSociodemographics.getItems().clear();
//
//        if (sociodemographicRecords.isEmpty()) {
//            groupItemEvaluationSociodemographics.getItems().add(new ItemEvaluation(R.drawable.action_quiz, TYPE_EMPTY,
//                    "Sociodemográfico", TypeEvaluation.SOCIODEMOGRAPHICS));
//        } else {
//            for (SociodemographicRecord sociodemographicRecord : sociodemographicRecords) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Sociodemográfico", "Respostas:  ", sociodemographicRecord.getCreatedAt(),
//                        TypeEvaluation.SOCIODEMOGRAPHICS);
//                itemEvaluation.setSociodemographicRecord(sociodemographicRecord);
//                groupItemEvaluationSociodemographics.getItems().add(itemEvaluation);
//            }
//        }
//        evaluationAdapter.notifyDataSetChanged();
//    }
//
//    private void prepareSleepHatits(List<SleepHabit> sleepHabits) {
//        groupItemEvaluationSleepHabits.getItems().clear();
//
//        if (sleepHabits.isEmpty()) {
//            groupItemEvaluationSleepHabits.getItems().add(new ItemEvaluation(R.drawable.action_quiz, TYPE_EMPTY,
//                    "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS));
//        } else {
//            for (SleepHabit sleepHabit : sleepHabits) {
//                ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_QUIZ,
//                        "Hábitos do Sono", "Respostas:  ", sleepHabit.getCreatedAt(),
//                        TypeEvaluation.SLEEP_HABITS);
//                itemEvaluation.setSleepHabit(sleepHabit);
//                groupItemEvaluationSleepHabits.getItems().add(itemEvaluation);
//            }
//        }
//        evaluationAdapter.notifyDataSetChanged();
//    }

    private void prepareData(List data, int type) {
        GroupItemEvaluation groupItemEvaluation = getGroupEvaluationByType(type);

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
                    Log.i("AAAAA", "Medical records");
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
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case TEMPERATURE:
                case WEIGHT:
                case WAIST_CIRCUMFERENCE:
                case HEIGHT:
                case FAT:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setValueMeasurement(String.valueOf(measurement.getValue()));
                        itemEvaluation.setUnitMeasurement(measurement.getUnit());
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
                case HEARTRATE:
                    for (Measurement measurement : (List<Measurement>) data) {
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
//                        itemEvaluation.setValueMeasurement(measurement.getDataset().get(0).getValue() + " - "+measurement.getDataset().get(0).getTimestamp());
                        itemEvaluation.setUnitMeasurement(measurement.getUnit());
                        groupItemEvaluation.getItems().add(itemEvaluation);
                    }
                    break;
            }
        }
        evaluationAdapter.notifyDataSetChanged();
    }

    /////////////////////////////////
    private void errorHandler(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);
            Log.i("AAA", httpEx.getMessage());
        }
        // message 500
    }

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

        //TODO Mudar o dateStart e o dateEnd
        DisposableManager.add(haniotNetRepository
                .getAllMeasurementsByType(helper.getLastPatient().get_id()
                        , "heart_rate", "created_at", "created_at", "created_at", 1, 1000)
                .subscribe(measurements ->
                        prepareData(measurements, HEARTRATE), this::errorHandler));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurementsByType(helper.getLastPatient().get_id()
                        , "blood_pressure", "created_at", "created_at", "created_at", 1, 1000)
                .subscribe(measurements ->
                        prepareData(measurements, BLOOD_PRESSURE), this::errorHandler));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurementsByType(helper.getLastPatient().get_id()
                        , "weight", "created_at", "created_at", "created_at", 1, 1000)
                .subscribe(measurements ->
                        prepareData(measurements, WEIGHT), this::errorHandler));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurementsByType(helper.getLastPatient().get_id()
                        , "glucose", "created_at", "created_at", "created_at", 1, 1000)
                .subscribe(measurements ->
                        prepareData(measurements, GLUCOSE), this::errorHandler));
    }

    //////////////
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
        evaluationAdapter.notifyDataSetChanged();
    }
}
