package br.edu.uepb.nutes.haniot.activity.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.base.BaseChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.base.CreateChart;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.server.SynchronizationServer.context;

/**
 * BloodPresssureChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SmartBandChartActivity extends BaseChartActivity {

    private CreateChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.smart_band));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabDay.setOnClickListener(this);
        fabWeek.setOnClickListener(this);
        fabMonth.setOnClickListener(this);
        fabYear.setOnClickListener(this);

        super.session = new Session(this);
        super.params = new Params(session.get_idLogged(), MeasurementType.STEPS);

        Chart barChart = (BarChart) findViewById(R.id.barChart);
        mChart = new CreateChart.Params(this, barChart)
                .yAxisEnabled(false)
                .xAxisStyle(Color.WHITE, XAxis.XAxisPosition.BOTTOM)
                .yAxisStyle(Color.WHITE)
                .setTextValuesColor(Color.WHITE)
                .formatDate(getString(R.string.date_format_month_day))
                .drawCircleStyle(ContextCompat.getColor(context, R.color.colorIndigo), ContextCompat.getColor(context, R.color.colorPrimary))
                .lineStyle(2.5f, ContextCompat.getColor(context, R.color.colorIndigo))
                .highlightStyle(Color.TRANSPARENT, 0.7f)
                .build();

        requestData(CHART_TYPE_MONTH);
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

        List<Measurement> points = agroupDay(data, Calendar.DATE);
        Calendar c = Calendar.getInstance();
        String dateFormat = "";

        switch (currentChartType){
            case CHART_TYPE_DAY:
                dateFormat = getString(R.string.date_format);
                break;
            case CHART_TYPE_SEVEN:
                points = agroup(points, Calendar.WEEK_OF_YEAR);
                dateFormat = getString(R.string.date_format);
                break;
            case CHART_TYPE_MONTH:
                points = agroup(points, Calendar.MONTH);
                dateFormat = getString(R.string.month);
                break;
            case CHART_TYPE_YEAR:
                points = agroup(points, Calendar.YEAR);
                dateFormat = getString(R.string.year);
                break;
        }


        mChart.paintBar(points, dateFormat);
        for (Measurement measurement : data) Log.d("A", DateUtils.formatDate(measurement.getRegistrationDate(), getString(R.string.date_format))+" - " + measurement.getValue());
        for (Measurement measurement : points) Log.d("B", DateUtils.formatDate(measurement.getRegistrationDate(), getString(R.string.date_format))+" - " + measurement.getValue());



    }

    public List<Measurement> agroupDay(List<Measurement> data, int type){
        List<Measurement> points = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        points.add(data.get(data.size()-1));
        for (int i = data.size()-1; i >= 0; i--) {
            c.setTimeInMillis(data.get(i).getRegistrationDate());
            int current = c.get(type);

            c.setTimeInMillis(points.get(points.size()-1).getRegistrationDate());
            int compare = c.get(type);

            if (current != compare) points.add(data.get(i));
        }

        return points;
    }

    public List<Measurement> agroup(List<Measurement> data, int type){
        List<Measurement> points = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        points.add(data.get(data.size()-1));
        int count = 0;
        int total = 0;
        long date = 0;
        for (int i = data.size()-1; i >= 0; i--) {
            c.setTimeInMillis(data.get(i).getRegistrationDate());
            int current = c.get(type);

            c.setTimeInMillis(points.get(points.size()-1).getRegistrationDate());
            int compare = c.get(type);

            date = data.get(i).getRegistrationDate();

            if (current != compare) {
                points.add(new Measurement(total, data.get(0).getUnit(), date, data.get(0).getTypeId()));
                count++;
                total = 0;
            }

            total += data.get(i).getValue();
        }

        return points;
    }

    @Override
    protected void requestData(int type) {
        if (type == CHART_TYPE_DAY) {
            currentChartType = CHART_TYPE_DAY;
        } else if (type == CHART_TYPE_SEVEN) {
            currentChartType = CHART_TYPE_SEVEN;
        } else if (type == CHART_TYPE_MONTH) {
            currentChartType = CHART_TYPE_MONTH;
        } else if (type == CHART_TYPE_YEAR) {
            currentChartType = CHART_TYPE_YEAR;
        }
        requestDataInServer("");
    }
}
