package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);

        initResources();
        initViews();

        prepareGlucoseMeasurement();
        prepareBloodPressureMeasurement();
        prepareHeartRateMeasurement();
        prepareWeightMeasurement();
        prepareQuizNutritionEvaluation();
        prepareQuizOdontoEvaluation();
        initRecyclerView();
    }

    private void initRecyclerView() {
        evaluationAdapter = new EvaluationExpandableAdapter(groupItemEvaluations, this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluationAdapter.setListener(this);
        evaluation.setAdapter(evaluationAdapter);
    }

    private void initViews() {

        String type = getIntent().getStringExtra("type");
        String nameType = "";
        if (type.equals("odonto")) nameType = "Odontológica";
        else if (type.equals("nutrition")) nameType = "Nutricional";
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
//        itemEvaluations = new ArrayList<>();
        patient = helper.getLastPatient();

        sendEvaluation.setOnClickListener(v -> {
        });
    }

    private void prepareHeartRateMeasurement() {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        //if (itemEvaluations.isEmpty()) {
        if (true) {
            ItemEvaluation itemEvaluation = new ItemEvaluation();
            itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
            itemEvaluation.setIcon(R.drawable.xcardiogram);
            itemEvaluation.setTitle("Frequência Cardíaca");
            itemEvaluation.setTypeEvaluation(TypeEvaluation.HEARTRATE);
            itemEvaluation.setQuizText("Sem registros! Clique para inserir.");
            itemEvaluations.add(itemEvaluation);
        } else {

            for (int i = 0; i <= 3; i++) {
                //Teste
                ItemEvaluation itemEvaluation = new ItemEvaluation();
                itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
                itemEvaluation.setIcon(R.drawable.xcardiogram);
                itemEvaluation.setTitle("Frequência Cardíaca");
                itemEvaluation.setTypeEvaluation(TypeEvaluation.HEARTRATE);
                itemEvaluation.setQuizText("Sem registros! Clique para inserir.");
                itemEvaluations.add(itemEvaluation);
            }
        }
        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(getString(R.string.heart_rate), itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);

    }


    private void prepareBloodPressureMeasurement() {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        //if (itemEvaluations.isEmpty()) {
        if (true) {
            ItemEvaluation itemEvaluation = new ItemEvaluation();
            itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
            itemEvaluation.setIcon(R.drawable.xblood_pressure);
            itemEvaluation.setTypeEvaluation(TypeEvaluation.BLOOD_PRESSURE);
            itemEvaluation.setTitle("Pressão Arterial");
            itemEvaluation.setQuizText("Sem registros! Clique para inserir.");
            itemEvaluations.add(itemEvaluation);
        } else {

            for (int i = 0; i <= 3; i++) {

                ItemEvaluation itemEvaluation = new ItemEvaluation();

                itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_MEASUREMENT);
                itemEvaluation.setIcon(R.drawable.xblood_pressure);
                itemEvaluation.setTypeEvaluation(TypeEvaluation.BLOOD_PRESSURE);
                itemEvaluation.setTitle("Pressão Arterial");
                itemEvaluation.setTime("12:23");
                itemEvaluation.setDate("28 de Abril, 2019");
                itemEvaluation.setValueMeasurement("76");
                itemEvaluation.setUnitMeasurement(getString(R.string.unit_pressure));
                itemEvaluations.add(itemEvaluation);
            }

        }
        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(getString(R.string.blood_pressure), itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);

    }


    private void prepareWeightMeasurement() {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        //if (itemEvaluations.isEmpty()) {
        if (true) {
            ItemEvaluation itemEvaluation = new ItemEvaluation();
            itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
            itemEvaluation.setIcon(R.drawable.xweight);
            itemEvaluation.setTypeEvaluation(TypeEvaluation.WEIGHT);
            itemEvaluation.setTitle("Peso");
            itemEvaluation.setQuizText("Sem registros! Clique para inserir.");
            itemEvaluations.add(itemEvaluation);
        } else {

            for (int i = 0; i <= 3; i++) {

                ItemEvaluation itemEvaluation = new ItemEvaluation();

                itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_MEASUREMENT);
                itemEvaluation.setIcon(R.drawable.xweight);
                itemEvaluation.setTypeEvaluation(TypeEvaluation.WEIGHT);
                itemEvaluation.setTitle("Peso");
                itemEvaluation.setTime("12:23");
                itemEvaluation.setDate("28 de Abril, 2019");
                itemEvaluation.setValueMeasurement("76");
                itemEvaluation.setUnitMeasurement("kg");
                itemEvaluations.add(itemEvaluation);
            }
        }
        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(getString(R.string.weight), itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);

    }

    private void prepareGlucoseMeasurement() {
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
        //TESTE
        for (int i = 0; i <= 3; i++) {
            ItemEvaluation itemEvaluation = new ItemEvaluation();
            itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_MEASUREMENT);
            itemEvaluation.setIcon(R.drawable.xglucosemeter);
            itemEvaluation.setTypeEvaluation(TypeEvaluation.GLUCOSE);
            itemEvaluation.setTitle("Glucose");
            itemEvaluation.setTime("12:28");
            itemEvaluation.setDate("28 de Abril, 2019");
            itemEvaluation.setValueMeasurement("543");
            itemEvaluation.setUnitMeasurement(getString(R.string.unit_glucose_mg_dL));
            itemEvaluations.add(itemEvaluation);
        }
        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(getString(R.string.glucose), itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);

    }

    private void prepareQuizOdontoEvaluation() {

        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        ItemEvaluation itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
//        itemEvaluation.setTypeEvaluation(TypeEvaluation.FAMILY_COHESION);
        itemEvaluation.setTitle("Coesão Familiar");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal balb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTypeEvaluation(TypeEvaluation.ORAL_HEALTH);
        itemEvaluation.setTitle("Saúde Bucal");
        itemEvaluation.setTime("12:34");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal b sds  dsd dsd sd sds alb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);


        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTypeEvaluation(TypeEvaluation.SOCIODEMOGRAPHICS);
        itemEvaluation.setTitle("Sociodemográfico");
        itemEvaluations.add(itemEvaluation);

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation("Questionário Odontológico", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);

    }

    private void prepareQuizNutritionEvaluation() {

        //TESTE TODO get no servidor
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        ItemEvaluation itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Hábitos Alimentares");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal balb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Hábitos do Sono");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal balb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Hábitos do Sono");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal balb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);


        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Hábitos Físicos");
        itemEvaluations.add(itemEvaluation);

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation("Questionário Nutricional", itemEvaluations);
        groupItemEvaluations.add(groupItemEvaluation);
    }

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
            case TypeEvaluation
                    .GLUCOSE:
                intent = new Intent(this, GlucoseActivity.class);
                break;
            case TypeEvaluation
                    .HEARTRATE:
                intent = new Intent(this, HeartRateActivity.class);
                break;
            case TypeEvaluation
                    .BLOOD_PRESSURE:
                intent = new Intent(this, BloodPressureHDPActivity.class);
                break;

            case TypeEvaluation
                    .WEIGHT:
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

        Log.i("AAA", "indo para " + name);
        startActivity(intent);
    }
}
