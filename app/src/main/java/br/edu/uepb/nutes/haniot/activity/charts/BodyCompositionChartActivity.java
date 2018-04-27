package br.edu.uepb.nutes.haniot.activity.charts;

import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.base.BaseChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.base.CreateChart;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import butterknife.ButterKnife;

/**
 * BodyCompositionChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BodyCompositionChartActivity extends BaseChartActivity {
    private CreateChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.body_composition));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonDay.setOnClickListener(this);
        mButtonMonth.setOnClickListener(this);
        mButtonWeek.setOnClickListener(this);

        super.session = new Session(this);
        super.params = new Params(session.get_idLogged(), MeasurementType.BODY_MASS);

        Chart lineChart = (LineChart) findViewById(R.id.chart);
        mChart = new CreateChart.Params(this, lineChart)
                .build();
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