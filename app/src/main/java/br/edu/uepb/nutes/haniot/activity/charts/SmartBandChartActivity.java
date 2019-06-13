package br.edu.uepb.nutes.haniot.activity.charts;

import android.graphics.Color;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.base.BaseChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.base.CreateChart;
import br.edu.uepb.nutes.haniot.activity.charts.base.InfoAdapter;
import br.edu.uepb.nutes.haniot.activity.charts.base.InfoMeasurement;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;

/**
 * BloodPresssureChartActivity implementation.
 *
 * @author Izabella, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SmartBandChartActivity extends BaseChartActivity {

    private CreateChart mChart;
    Chart barChart;
    List<Measurement> points;

    @Override
    public void initView() {
        getSupportActionBar().setTitle(getString(R.string.smart_band));

        barChart = (BarChart) findViewById(R.id.barChart);
        mChart = new CreateChart.Params(this, barChart)
                .lineStyle(2.5f, Color.WHITE)
                .drawCircleStyle(Color.WHITE, getResources().getColor(R.color.colorPrimary))
                .yAxisEnabled(false)
                .xAxisStyle(Color.WHITE, XAxis.XAxisPosition.BOTTOM)
                .yAxisStyle(Color.WHITE)
                .setTextValuesColor(Color.WHITE)
                .colorFontDescription(Color.WHITE)
                .highlightStyle(Color.TRANSPARENT, 0.7f)
                .setRangeY(35, 38)
                .formatDate(getString(R.string.date_format_month_day))
                .build();

        requestData(CHART_TYPE_MONTH);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //              Measurement m = (Measurement) e.getData();
                //  Log.d("TESTE", String.valueOf(points.get(3).getValue()));
                Log.d("TESTE", String.valueOf(e.getX()) + " - " + String.valueOf(e.getY()));

                ArrayList<Measurement> a = new ArrayList<>();
                a.add(points.get((int) e.getX()));
                createMoreInfo(a);
                float x = e.getX();
                float y = e.getY();
            }

            @Override
            public void onNothingSelected() {

                createMoreInfo(new ArrayList<>());
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.activity_bar_chart;
    }

    @Override
    public String getTypeMeasurement() {
        return null;
        // steps
    }

    @Override
    public Chart getChart() {
        return barChart;
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

        points = agroupDay(data);
        Calendar c = Calendar.getInstance();
        String dateFormat = "";

        switch (currentChartType) {
            case CHART_TYPE_DAY:
                dateFormat = getString(R.string.date_format);
                Collections.reverse(points);
                break;
            case CHART_TYPE_SEVEN:
                points = agroup(points, Calendar.WEEK_OF_YEAR);
                dateFormat = getString(R.string.date_format_week);
                break;
            case CHART_TYPE_MONTH:
                points = agroup(points, Calendar.MONTH);
                dateFormat = getString(R.string.date_format_month);
                break;
            case CHART_TYPE_YEAR:
                points = agroup(points, Calendar.YEAR);
                dateFormat = getString(R.string.date_format_year);
                break;
        }


        mChart.paintBar(points, dateFormat);
        mProgressBar.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);

        //Teste
//        for (Measurement measurement : data)
//            Log.d("A", DateUtils.formatDate(measurement.getRegistrationDate(), getString(R.string.date_format)) + " - " + measurement.getValue());
//        for (Measurement measurement : points)
//            Log.d("B", DateUtils.formatDate(measurement.getRegistrationDate(), getString(R.string.date_format)) + " - " + measurement.getValue());

    }

    public List<Measurement> agroupDay(List<Measurement> data) {
        List<Measurement> points = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        points.add(data.get(data.size() - 1));
//        for (int i = data.size() - 1; i >= 0; i--) {
//            c.setTimeInMillis(data.get(i).getRegistrationDate());
//            int current = c.get(Calendar.DATE);
//
//            c.setTimeInMillis(points.get(points.size() - 1).getRegistrationDate());
//            int compare = c.get(Calendar.DATE);
//
//            if (current != compare) points.add(data.get(i));
//        }

        return points;
    }

    public List<Measurement> agroup(List<Measurement> data, int type) {
        List<Measurement> points = new ArrayList<>();
        Calendar c;
        int total = 0;
        int totalDist = 0;
        int totalCal = 0;
//        if (type == Calendar.YEAR) {
//            for (int i = 0; i <= data.size() - 1; i++) {
//                total += data.get(i).getValue();
//                if (data.get(i).getMeasurementList().get(0).getTypeId() == MeasurementType.DISTANCE) {
//                    totalCal += data.get(i).getMeasurementList().get(0).getValue();
//                    totalDist += data.get(i).getMeasurementList().get(1).getValue();
//                } else {
//                    totalCal += data.get(i).getMeasurementList().get(1).getValue();
//                    totalDist += data.get(i).getMeasurementList().get(0).getValue();
//                }
//            }
//
//            Measurement measurement = data.get(0);
//            measurement.setValue(total);
//            measurement.addMeasurement(
//                    new Measurement(totalDist, getString(R.string.unit_meters), MeasurementType.DISTANCE),
//                    new Measurement(totalCal, getString(R.string.unit_kcal), MeasurementType.CALORIES_BURNED)
//            );
//
//            points.add(measurement);
//        } else {
//            c = Calendar.getInstance();
//            points.add(data.get(data.size() - 1));
//
//            for (int i = data.size() - 1; i >= 0; i--) {
//                c.setTimeInMillis(data.get(i).getRegistrationDate());
//                int current = c.get(type);
//
//                c.setTimeInMillis(points.get(points.size() - 1).getRegistrationDate());
//                int compare = c.get(type);
//
//                total += data.get(i).getValue();
//                if (data.get(i).getMeasurementList().get(0).getTypeId() == MeasurementType.DISTANCE) {
//                    totalCal += data.get(i).getMeasurementList().get(0).getValue();
//                    totalDist += data.get(i).getMeasurementList().get(1).getValue();
//                } else {
//                    totalCal += data.get(i).getMeasurementList().get(1).getValue();
//                    totalDist += data.get(i).getMeasurementList().get(0).getValue();
//                }
//
//                if (current != compare) {
//                    Measurement measurement = data.get(i);
//                    measurement.setValue(total);
//                    measurement.addMeasurement(
//                            new Measurement(totalDist, "m", MeasurementType.DISTANCE),
//                            new Measurement(totalCal, "kcal", MeasurementType.CALORIES_BURNED)
//                    );
//
//                    points.add(measurement);
//                    total = 0;
//                    totalDist = 0;
//                    totalCal = 0;
//                }
//
//            }
//        }
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

    @Override
    public void createMoreInfo(List<Measurement> measurements) {
        android.util.Log.d("Smart", "createMoreInfo");
        ArrayList<InfoMeasurement> infoMeasurements = new ArrayList<>();
        infoMeasurements.addAll(getInfosBase(measurements));

        GridView gridView = (GridView) findViewById(R.id.moreinfo_grid);
        InfoAdapter infoAdapter = new InfoAdapter(this, infoMeasurements);
        gridView.setAdapter(infoAdapter);
    }

    @Override
    protected ArrayList<InfoMeasurement> getInfosBase(List<Measurement> measurements) {
        ArrayList<InfoMeasurement> infos = new ArrayList<>();

        if (measurements.isEmpty()) {
            infos.add(new InfoMeasurement(getString(R.string.info_steps), " - "));
            infos.add(new InfoMeasurement(getString(R.string.info_distance), " - "));
            infos.add(new InfoMeasurement(getString(R.string.info_calories), " - "));
            infos.add(new InfoMeasurement(getString(R.string.info_period), " - "));
        } else {
            infos.add(new InfoMeasurement(getString(R.string.info_steps), String.valueOf((int) measurements.get(0).getValue())));
//            infos.add(new InfoMeasurement(getString(R.string.info_distance), String.valueOf((int) (measurements.get(0).getMeasurementList().get(0).getValue())) + " m"));
//            infos.add(new InfoMeasurement(getString(R.string.info_calories), String.valueOf((int) (measurements.get(0).getMeasurementList().get(1).getValue())) + " kcal"));
//            infos.add(new InfoMeasurement(getString(R.string.info_period), DateUtils.formatDate(measurements.get(0).getRegistrationDate(), getString(R.string.date_format))));
        }
        return infos;
    }
}
