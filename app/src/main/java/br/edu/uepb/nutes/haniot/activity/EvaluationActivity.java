package br.edu.uepb.nutes.haniot.activity;

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
import br.edu.uepb.nutes.haniot.adapter.EvaluationAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Evaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluationActivity extends AppCompatActivity implements OnRecyclerViewListener<ItemEvaluation> {

    @BindView(R.id.list_evaluation)
    RecyclerView evaluation;
    EvaluationAdapter evaluationAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gender_icon)
    ImageView gender;
    @BindView(R.id.send_evaluation)
    FloatingActionButton sendEvaluation;
    @BindView(R.id.message_patient)
    TextView messagePatient;

    private AppPreferencesHelper helper;
    private List<ItemEvaluation> itemEvaluations;
    private Patient patient;
    HaniotNetRepository haniotNetRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);

        initResources();
        initViews();
        initRecyclerView();

        prepareMeasurementEvaluation();
        prepareQuizNutritionEvaluation();
        prepareQuizOdontoEvaluation();
        evaluationAdapter.addItems(itemEvaluations);

    }

    private String getTypeMeasurement(int type) {
        return null;
    }

    private int getIcon(int type) {
        //TODO implementar
        return R.drawable.xshape;
    }

    private void prepareMeasurementEvaluation() {

        //TESTE TODO get no servidor
        ItemEvaluation header = new ItemEvaluation();
        header.setTypeHeader(ItemEvaluation.TYPE_HEADER);
        header.setTitle("Medições");
        itemEvaluations.add(header);
//
//        for (Measurement measurement : measurementsServer) {
//            ItemEvaluation itemEvaluation = new ItemEvaluation();
//            itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
//            itemEvaluation.setIcon(getIcon(measurement.getTypeId()));
//            itemEvaluation.setTitle(getTypeMeasurement(measurement.getTypeId()));
//            itemEvaluation.setQuizText("Sem registros! Clique para inserir.");
//            itemEvaluations.add(itemEvaluation);
//        }

        ItemEvaluation itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_EMPTY);
        itemEvaluation.setIcon(R.drawable.xcardiogram);
        itemEvaluation.setTitle("Frequência Cardíaca");
        itemEvaluation.setQuizText("Sem registros! Clique para inserir.");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_MEASUREMENT);
        itemEvaluation.setIcon(R.drawable.xweight);
        itemEvaluation.setTitle("Peso");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setValueMeasurement("76");
        itemEvaluation.setUnitMeasurement("kg");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_MEASUREMENT);
        itemEvaluation.setIcon(R.drawable.xweight);
        itemEvaluation.setTitle("Peso");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("29 de Abril, 2019");
        itemEvaluation.setValueMeasurement("86");
        itemEvaluation.setUnitMeasurement("kg");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_MEASUREMENT);
        itemEvaluation.setIcon(R.drawable.xglucosemeter);
        itemEvaluation.setTitle("Glucose");
        itemEvaluation.setTime("12:28");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setValueMeasurement("543");
        itemEvaluation.setUnitMeasurement(getString(R.string.unit_glucose_mg_dL));
        itemEvaluations.add(itemEvaluation);

    }

    private void prepareQuizOdontoEvaluation() {

        //TESTE TODO get no servidor
        ItemEvaluation header = new ItemEvaluation();
        header.setTypeHeader(ItemEvaluation.TYPE_HEADER);
        header.setTitle("Questionário Odontológico");
        itemEvaluations.add(header);

        ItemEvaluation itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Questionário Odontológico");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal balb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Questionário Odontológico");
        itemEvaluation.setTime("12:34");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal b sds  dsd dsd sd sds alb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);

    }

    private void prepareQuizNutritionEvaluation() {
//        DisposableManager.add(haniotNetRepository
//                .getAllMeasurements(patient.get_id(), ));

        //TESTE TODO get no servidor

        //HEADER
        ItemEvaluation header = new ItemEvaluation();
        header.setTypeHeader(ItemEvaluation.TYPE_HEADER);
        header.setTitle("Questionário Nutricional");
        itemEvaluations.add(header);

        ItemEvaluation itemEvaluation = new ItemEvaluation();
        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setTypeHeader(ItemEvaluation.TYPE_QUIZ);
        itemEvaluation.setIcon(R.drawable.action_quiz);
        itemEvaluation.setTitle("Questionário Nutricional");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setDate("28 de Abril, 2019");
        itemEvaluation.setQuizText("Respostas: bla bla bla bla bla bal basadfasdasdasdasdasdasdas dfsdd sdfv s s dlb alba sfgsdfsd sdfsdjfsdf sdfjsd fsdkjf sdkjfsdfsdfs ");
        itemEvaluations.add(itemEvaluation);

    }

    private Evaluation prepareEvaluation() {
        for (ItemEvaluation itemEvaluation : itemEvaluations) {
            if (itemEvaluation.isChecked()) {

            }
        }
        return null;
    }


    private void initRecyclerView() {
        evaluationAdapter = new EvaluationAdapter(this);
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
        helper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        itemEvaluations = new ArrayList<>();
        patient = helper.getLastPatient();

        sendEvaluation.setOnClickListener(v -> {
            sendEvaluation(prepareEvaluation());
        });
    }

    private void sendEvaluation(Evaluation evaluation) {
//
//        DisposableManager.add(haniotNetRepository.
//                saveEvaluation());
        finish();
    }

    @Override
    public void onItemClick(ItemEvaluation item) {
        item.setChecked(!item.isChecked());
    }

    @Override
    public void onLongItemClick(View v, ItemEvaluation item) {

    }

    @Override
    public void onMenuContextClick(View v, ItemEvaluation item) {

    }
}
