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
 * GlucoseChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GlucoseChartActivity extends BaseChartActivity {

    private final float PRE_DIABETES = 200;
    private CreateChart mChart;
    Chart lineChart;

    @Override
    public void initView() {
        getSupportActionBar().setTitle(getString(R.string.glucose));

        lineChart = (LineChart) findViewById(R.id.chart);
        mChart = new CreateChart.Params(this, lineChart)
                .lineStyle(2.5f, Color.WHITE)
                .drawCircleStyle(Color.WHITE, getResources().getColor(R.color.colorPrimary))
                .yAxisEnabled(false)
                .xAxisStyle(Color.WHITE, XAxis.XAxisPosition.BOTTOM)
                .yAxisStyle(Color.WHITE)
                .setTextValuesColor(Color.WHITE)
                .colorFontDescription(Color.WHITE)
                .highlightStyle(Color.TRANSPARENT, 0.7f)
                .createLimit("Indício de diabetes", PRE_DIABETES, Color.RED)
                .build();

        requestData(CHART_TYPE_MONTH);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_line_chart;
    }

    @Override
    public String getTypeMeasurement() {
        return MeasurementType.BLOOD_GLUCOSE;
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