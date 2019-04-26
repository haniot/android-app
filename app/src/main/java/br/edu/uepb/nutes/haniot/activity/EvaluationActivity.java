package br.edu.uepb.nutes.haniot.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.EvaluationAdapter;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluationActivity extends AppCompatActivity {

    @BindView(R.id.list_evaluation)
    RecyclerView evaluation;
    EvaluationAdapter evaluationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);

        evaluationAdapter = new EvaluationAdapter(this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluation.setAdapter(evaluationAdapter);
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();
        ItemEvaluation itemEvaluation = new ItemEvaluation();
        itemEvaluation.setIcon(R.drawable.xcardiogram);
        itemEvaluation.setTitle("Frequência Cardíaca");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setValueMeasurement("98");
        itemEvaluation.setUnitMeasurement("bpm");
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation();
        itemEvaluation.setIcon(R.drawable.xweight);
        itemEvaluation.setTitle("Peso");
        itemEvaluation.setTime("12:23");
        itemEvaluation.setValueMeasurement("76");
        itemEvaluation.setUnitMeasurement("kg");
        itemEvaluations.add(itemEvaluation);

        evaluationAdapter.addItems(itemEvaluations);

    }
}
