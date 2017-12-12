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
import br.edu.uepb.nutes.haniot.model.MeasurementScale;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementScaleDAO;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by izabella on 23/10/17.
 */

public class ScaleGraphBodyFatActivity extends AppCompatActivity {

    private Session session;

    @BindView(R.id.chart_scale_bodyfat)
    LineChart mChartBodyFat;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_graph_bodyfat);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("BodyFat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(this);

        createChart();
    }

    private void createChart() {

        mChartBodyFat.getDescription().setEnabled(false);
        mChartBodyFat.setDrawGridBackground(false);

        YAxis leftAxis = mChartBodyFat.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);

        mChartBodyFat.getAxisRight().setEnabled(false);
//
        long dayMile = (24 * 60 * 60 * 1000);
        long dateEnd = DateUtils.getCurrentDatetime();
        long dateStart = dateEnd - (dayMile * 7);
        String deviceAddress = "D4:36:39:91:75:71";
        String userId = session.getIdLogged();

//String.valueOf(dateEnd)

        //ArrayList<MeasurementScale> measurementData = new ArrayList<MeasurementScale>();
        List<MeasurementScale> measurementData= MeasurementScaleDAO.getInstance(this).filter(dateStart, dateEnd, deviceAddress, userId);

//        MeasurementScale m1 = new MeasurementScale();
//        m1.setWeight(70);
//        m1.setRegistrationTime(dateStart);
//
//        MeasurementScale m2 = new MeasurementScale();
//        m2.setWeight(60);
//        m2.setRegistrationTime(dateStart + (dayMile * 2));
//
//        MeasurementScale m3 = new MeasurementScale();
//        m3.setWeight(60);
//        m3.setRegistrationTime(dateStart + (dayMile * 3));
//
//        MeasurementScale m4 = new MeasurementScale();
//        m4.setWeight(65.6f);
//        m4.setRegistrationTime(dateStart + (dayMile * 4));
//
//        MeasurementScale m5 = new MeasurementScale();
//        m5.setWeight(57.67f);
//        m5.setRegistrationTime(dateStart + (dayMile * 5));
//
//        MeasurementScale m6 = new MeasurementScale();
//        m6.setWeight(90);
//        m6.setRegistrationTime(dateStart + (dayMile * 6));
//
//        MeasurementScale m7 = new MeasurementScale();
//        m7.setWeight(80);
//        m7.setRegistrationTime(dateEnd);
//
//        measurementData.add(m1);
//        measurementData.add(m2);
//        measurementData.add(m3);
//        measurementData.add(m4);
//        measurementData.add(m5);
//        measurementData.add(m6);
//        measurementData.add(m7);

        if(measurementData.size() == 0) return;

        final String[] quarters = new String[measurementData.size()];
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i < measurementData.size(); i++) {
            String format = "dd/MM";
            String date =  DateUtils.getDatetime(measurementData.get(i).getRegistrationTime(), format);

            float bodyfat = measurementData.get(i).getBodyFat();
            entries.add(new Entry((float)i, bodyfat));
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

        XAxis xAxis = mChartBodyFat.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
//        xAxis.setSpaceMin(0);
//        xAxis.setSpaceMax(measurementData.size()-1);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        LineDataSet set = new LineDataSet(entries, "body fat");
        set.setLineWidth(3f);
        set.setDrawCircles(true);
        set.setDrawCircleHole(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSets.add(set);
        LineData data = new LineData(dataSets);

        mChartBodyFat.animate();
        mChartBodyFat.setData(data);
        mChartBodyFat.setEnabled(true);
        mChartBodyFat.invalidate();
        mChartBodyFat.setVisibleXRangeMaximum(65f);
        mChartBodyFat.resetViewPortOffsets();

        mChartBodyFat.animateX(3000);
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
