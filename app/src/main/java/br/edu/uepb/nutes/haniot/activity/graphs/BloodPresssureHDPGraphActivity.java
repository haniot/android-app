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

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by izabella on 10/11/17.
 * Não concluida
 */

public class BloodPresssureHDPGraphActivity extends AppCompatActivity {
    private Session session;

    @BindView(R.id.chart_blood_pressure)
    LineChart mChart;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_hdp_graph);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blood Pressure");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(this);

        createChart();
    }

    private void createChart() {

        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(50);
        leftAxis.setAxisMaximum(140);
        mChart.getAxisRight().setEnabled(false);
//
        long dayMile = (24 * 60 * 60 * 1000);
        long dateEnd = DateUtils.getCurrentDatetime();
        long dateStart = dateEnd - (dayMile * 7);
        String deviceAddress = "1C:87:74:01:73:10";
//        String userId = session.getIdLogged();

//String.valueOf(dateEnd)

//        ArrayList<MeasurementBloodPressure> measurementData = new ArrayList<MeasurementBloodPressure>();
//        List<MeasurementBloodPressure> measurementData= MeasurementBloodPressure.getInstance(this).filter(dateStart, dateEnd, deviceAddress, userId);
//
//        MeasurementBloodPressure m1 = new MeasurementBloodPressure();
//        m1.setHeartFate(70);
//        m1.setRegistrationTime(dateStart);
//
//        MeasurementBloodPressure m2 = new MeasurementBloodPressure();
//        m2.setHeartFate(60);
//        m2.setRegistrationTime(dateStart + (dayMile * 2));
//
//        MeasurementBloodPressure m3 = new MeasurementBloodPressure();
//        m3.setHeartFate(60);
//        m3.setRegistrationTime(dateStart + (dayMile * 3));
//
//        MeasurementBloodPressure m4 = new MeasurementBloodPressure();
//        m4.setHeartFate(65);
//        m4.setRegistrationTime(dateStart + (dayMile * 4));
//
//        MeasurementBloodPressure m5 = new MeasurementBloodPressure();
//        m5.setHeartFate(57);
//        m5.setRegistrationTime(dateStart + (dayMile * 5));
//
//        MeasurementBloodPressure m6 = new MeasurementBloodPressure();
//        m6.setHeartFate(90);
//        m6.setRegistrationTime(dateStart + (dayMile * 6));
//
//        MeasurementBloodPressure m7 = new MeasurementBloodPressure();
//        m7.setHeartFate(80);
//        m7.setRegistrationTime(dateEnd);
//
//        measurementData.add(m1);
//        measurementData.add(m2);
//        measurementData.add(m3);
//        measurementData.add(m4);
//        measurementData.add(m5);
//        measurementData.add(m6);
//        measurementData.add(m7);

//        if(measurementData.size() == 0) return;
//
//        final String[] quarters = new String[measurementData.size()];
//        ArrayList<Entry> entries = new ArrayList<Entry>();
//
//        for(int i = 0; i < measurementData.size(); i++) {
//            String format = "dd/MM";
//            String date =  DateUtils.formatDate(measurementData.get(i).getRegistrationTime(), format);
//            float temperature = measurementData.get(i).getHeartFate(); //get
//            entries.add(new Entry(i, temperature));
//            quarters[i] = date;
//        }
//
//        IAxisValueFormatter formatter = new IAxisValueFormatter() {
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if(value >= quarters.length){return "";}
//                return quarters[(int) value];
//            }
//
//            // we don't draw numbers, so no decimal digits needed
//            public int getDecimalDigits() {  return 0; }
//        };
//
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
//        xAxis.setValueFormatter(formatter);
////        xAxis.setSpaceMin(0);
////        xAxis.setSpaceMax(measurementData.size()-1);
//        xAxis.setEnabled(true);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//
//        LineDataSet set = new LineDataSet(entries, "blood pressure");
//        set.setLineWidth(3f);
//        set.setDrawCircles(true);
//        set.setDrawCircleHole(true);
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//
//        dataSets.add(set);
//        LineData data = new LineData(dataSets);
//
//        mChart.animate();
//        mChart.setData(data);
//        mChart.setEnabled(true);
//        mChart.invalidate();
//        mChart.setVisibleXRangeMaximum(65f);
//        mChart.resetViewPortOffsets();
//
//        mChart.animateX(3000);
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
