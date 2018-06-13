package br.edu.uepb.nutes.haniot.activity.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.base.BaseChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.base.CreateChart;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.server.SynchronizationServer.context;

/**
 * BloodPresssureChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BloodPresssureChartActivity extends BaseChartActivity {

    private CreateChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.blood_pressure));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabDay.setOnClickListener(this);
        fabWeek.setOnClickListener(this);
        fabMonth.setOnClickListener(this);
        fabYear.setOnClickListener(this);

        super.session = new Session(this);
        super.params = new Params(session.get_idLogged(), MeasurementType.BLOOD_PRESSURE_SYSTOLIC);

        Chart lineChart = (LineChart) findViewById(R.id.chart);
        mChart = new CreateChart.Params(this, lineChart)
                .lineStyle(2.5f, ContextCompat.getColor(context, R.color.colorIndigo))
                .yAxisEnabled(false)
                .xAxisStyle(Color.WHITE, XAxis.XAxisPosition.BOTTOM)
                .yAxisStyle(Color.WHITE)
                .setTextValuesColor(Color.WHITE)
                .colorFont(Color.WHITE)
                .drawCircleStyle(ContextCompat.getColor(context, R.color.colorIndigo), ContextCompat.getColor(context, R.color.colorPrimary))
                .lineStyle(2.5f, ContextCompat.getColor(context, R.color.colorIndigo))
                .highlightStyle(Color.TRANSPARENT, 0.7f)
                .createLimit("Limit 1", 135.0f, Color.WHITE)
                .build();

        requestData(GRAPH_TYPE_MONTH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData(GRAPH_TYPE_DAY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateData(List<Measurement> data) {
        mChart.paint(data);
    }
}
