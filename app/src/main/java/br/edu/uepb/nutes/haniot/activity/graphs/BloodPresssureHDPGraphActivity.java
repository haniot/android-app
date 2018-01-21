package br.edu.uepb.nutes.haniot.activity.graphs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by izabella on 10/11/17.
 */

public class BloodPresssureHDPGraphActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "FragmentActivity";

    private final int GRAPH_TYPE_DAY = 1;
    private final int GRAPH_TYPE_SEVEN = 2;
    private final int GRAPH_TYPE_MONTH = 3;

    private Session session;
    private Params params;
    private List<Measurement> measurementData;

    @BindView(R.id.chart_blood_pressure)
    LineChart mChart;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.day_button)
    Button mButtonDay;

    @BindView(R.id.week_button)
    Button mButtonWeek;

    @BindView(R.id.month_button)
    Button mButtonMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_hdp_graph);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blood Pressure");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(this);

        mButtonDay.setOnClickListener(this);
        mButtonMonth.setOnClickListener(this);
        mButtonWeek.setOnClickListener(this);

        params = new Params(session.get_idLogged(), MeasurementType.BLOOD_PRESSURE_DIASTOLIC);

        createChart(GRAPH_TYPE_DAY);
    }

    private void createChart(int type) {
        if (type == GRAPH_TYPE_DAY)
            requestData("1d");
        else if (type == GRAPH_TYPE_SEVEN)
            requestData("1w");
        else if (type == GRAPH_TYPE_MONTH)
            requestData("1m");
    }

    private void requestData(String period) {
        Historical hist = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER) // required
                .params(params) // required
                .filterDate(period)
                .build();

        hist.request(this, new CallbackHistorical<Measurement>() {
            @Override
            public void onBeforeSend() {
                Log.w(TAG, "onBeforeSend()");
                // TODO Colocar loading
            }

            @Override
            public void onError(JSONObject result) {
                Log.w(TAG, "onError()");
                printMessage(getString(R.string.error_500));
            }

            @Override
            public void onResult(List<Measurement> result) {
                Log.w(TAG, "onSuccess()");

                measurementData.clear();
                measurementData.addAll(result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        paintChart();
                    }
                });
            }

            @Override
            public void onAfterSend() {
                Log.w(TAG, "onAfterSend()");
                // TODO Remover loading
            }
        });
    }

    /**
     *
     */
    private void paintChart() {
        final String[] quarters = new String[measurementData.size()];
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < measurementData.size(); i++) {
            String date = DateUtils.getDatetime(measurementData.get(i).getRegistrationDate(),
                    getString(R.string.date_format));

            float pressure = (float) measurementData.get(i).getValue();
            entries.add(new Entry((float) i, pressure));
            quarters[i] = date;
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= quarters.length) return "";
                return quarters[(int) value];
            }
        };
        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        LineDataSet set = new LineDataSet(entries, getString(R.string.blood_pressure));
        set.setLineWidth(3f);
        set.setDrawCircles(true);
        set.setDrawCircleHole(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSets.add(set);
        LineData data = new LineData(dataSets);

        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        mChart.getAxisRight().setEnabled(false);
        mChart.animate();
        mChart.setData(data);
        mChart.setEnabled(true);
        mChart.invalidate();
        mChart.setVisibleXRangeMaximum(65f);
        mChart.resetViewPortOffsets();
        mChart.animateX(200);

        mChart.notifyDataSetChanged();
    }

    private void printMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.day_button:
                createChart(GRAPH_TYPE_DAY);
                break;

            case R.id.month_button:
                createChart(GRAPH_TYPE_MONTH);
                break;

            case R.id.week_button:
                createChart(GRAPH_TYPE_SEVEN);
                break;
        }
    }
}
