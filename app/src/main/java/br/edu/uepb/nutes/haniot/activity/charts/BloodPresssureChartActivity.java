package br.edu.uepb.nutes.haniot.activity.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;

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
    Chart lineChart;

    @Override
    public void initView() {
        getSupportActionBar().setTitle(getString(R.string.blood_pressure));

        lineChart = (LineChart) findViewById(R.id.chart);
        mChart = new CreateChart.Params(this, lineChart)
                .yAxisEnabled(false)
                .xAxisStyle(Color.WHITE, XAxis.XAxisPosition.BOTTOM)
                .yAxisStyle(Color.WHITE)
                .setTextValuesColor(Color.WHITE)
                .drawCircleStyle(ContextCompat.getColor(context, R.color.colorIndigo), ContextCompat.getColor(context, R.color.colorPrimary))
                .lineStyle(2.5f, ContextCompat.getColor(context, R.color.colorIndigo))
                .highlightStyle(Color.TRANSPARENT, 0.7f)
                .addLegend(getString(R.string.systolic), getString(R.string.diastolic))
                .build();

        requestData(CHART_TYPE_MONTH);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_line_chart;
    }

    @Override
    public int getTypeMeasurement() {
        return MeasurementType.BLOOD_PRESSURE_SYSTOLIC;
    }

    @Override
    public Chart getChart() {
        return lineChart;
    }


    @Override
    protected void onResume() {
        super.onResume();
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
    public void onUpdateData(List<Measurement> data, int currentChartType) {
        mChart.paint(data);
        mProgressBar.setVisibility(View.INVISIBLE);
        lineChart.setVisibility(View.VISIBLE);
    }
}
