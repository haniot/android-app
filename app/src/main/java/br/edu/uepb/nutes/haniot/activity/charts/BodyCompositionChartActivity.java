package br.edu.uepb.nutes.haniot.activity.charts;

import android.graphics.Color;
import android.util.Log;
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
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;

/**
 * BodyCompositionChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BodyCompositionChartActivity extends BaseChartActivity {

    private final float OBESITY_OVERWEIGHT = 25;
    private CreateChart mChart;
    Chart lineChart;

    @Override
    public void initView() {
        getSupportActionBar().setTitle(getString(R.string.body_composition));

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
                .build();
        setLimitObesity();

        requestData(CHART_TYPE_MONTH);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_line_chart;
    }

    @Override
    public Chart getChart() {
        return lineChart;
    }

    @Override
    public String getTypeMeasurement() {
        return MeasurementType.BODY_MASS;
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

    /**
     * Configure the obesity limit in the chart
     */
    public void setLimitObesity() {
        runOnUiThread(() -> {
            DisposableManager.add(haniotNetRepository.
                    getAllMeasurementsByType(patient.get_id(), MeasurementType.HEIGHT,
                            "-timestamp", null, null, 1, 1)
                    .doAfterSuccess(measurements -> {
                        if (measurements != null && measurements.size() > 0) {
                            double height = measurements.get(0).getValue();

                            if (measurements.get(0).getUnit().equals("cm")) height /= 100;
                            float limit = (float) (OBESITY_OVERWEIGHT * Math.pow(height, 2));
                            mChart.getParams().createLimit(getString(R.string.limit_obesity), limit, Color.RED);
                            mChart.getmChart().invalidate();
                        }
                    })
                    .subscribe(measurements -> {
                    }, error -> Log.w("ChartBodyComposition", "onError()")));
        });

    }
}