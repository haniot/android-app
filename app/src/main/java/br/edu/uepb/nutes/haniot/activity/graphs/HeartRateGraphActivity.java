package br.edu.uepb.nutes.haniot.activity.graphs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.MeasurementHeartRate;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementHeartRateDAO;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by izabella on 01/11/17.
 */

public class HeartRateGraphActivity extends AppCompatActivity {

    private Session session;

    @BindView(R.id.chart_heart_rate)
    LineChart mChart;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_graph);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Heart Rate");
        session = new Session(this);


        createChart();
    }

    private void createChart() {

        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(150f);

        mChart.getAxisRight().setEnabled(false);
//
        long dayMile = (24 * 60 * 60 * 1000);
        long dateEnd = DateUtils.getCurrentDatetime();
        long dateStart = dateEnd - (dayMile * 7);
        String deviceAddress = "E9:50:60:1F:31:D2";
        String userId = session.getIdLogged();

        List<MeasurementHeartRate> measurementData= MeasurementHeartRateDAO.getInstance(this).filter(dateStart, dateEnd, deviceAddress, userId);

        if(measurementData.size() == 0) return;

        final String[] quarters = new String[measurementData.size()];
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i < measurementData.size(); i++) {
            String format = "dd/MM";
            String date =  DateUtils.getDatetime(measurementData.get(i).getRegistrationTime(), format);

            int heartRate = measurementData.get(i).getFcMaximum();
            entries.add(new Entry(i, heartRate));
            quarters[i] = date;
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value >= quarters.length){return "";}
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
//        xAxis.setSpaceMin(0);
//        xAxis.setSpaceMax(measurementData.size()-1);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        LineDataSet set = new LineDataSet(entries, "fc");
        set.setLineWidth(3f);
        set.setDrawCircles(true);
        set.setDrawCircleHole(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSets.add(set);
        LineData data = new LineData(dataSets);

        mChart.animate();
        mChart.setData(data);
        mChart.setEnabled(true);
        mChart.invalidate();
        mChart.setVisibleXRangeMaximum(65f);
        mChart.resetViewPortOffsets();
       // mChart.getDrawingTime();

        mChart.animateX(3000);
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
}
