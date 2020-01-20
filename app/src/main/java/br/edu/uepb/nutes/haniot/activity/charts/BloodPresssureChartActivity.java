package br.edu.uepb.nutes.haniot.activity.charts;

import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.base.BaseChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.base.CreateChart;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;

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
                .drawCircleStyle(getResources().getColor(R.color.colorIndigo), getResources().getColor(R.color.colorPrimary))
                .lineStyle(2.5f, getResources().getColor(R.color.colorIndigo))
                .highlightStyle(Color.TRANSPARENT, 0.7f)
                .createLimit(getString(R.string.limit_high_systolic), 140, getResources().getColor(R.color.colorRed))
                .createLimit(getString(R.string.limit_high_diastolic), 90, getResources().getColor(R.color.colorRed))
                .addLegend(getString(R.string.systolic), getString(R.string.diastolic))
                .build();

        requestData(CHART_TYPE_MONTH);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_line_chart;
    }

    @Override
    public String getTypeMeasurement() {
        return MeasurementType.BLOOD_PRESSURE;
    }

    @Override
    public Chart getChart() {
        return lineChart;
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
