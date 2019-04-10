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
 * HeartRateChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HeartRateChartActivity extends BaseChartActivity {

    private final float TACHYCARDIA = 110;
    private CreateChart mChart;
    Chart lineChart;

    @Override
    public void initView() {
        getSupportActionBar().setTitle(getString(R.string.heart_rate));

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
                .createLimit(getString(R.string.limit_heart_rate), TACHYCARDIA, Color.RED)
                .build();

        requestData(CHART_TYPE_MONTH);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_line_chart;
    }

    @Override
    public int getTypeMeasurement() {
        return MeasurementType.HEART_RATE;
    }

    @Override
    public Chart getChart() {
        return lineChart;
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestData(CHART_TYPE_DAY);
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
