package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.BloodPresssureChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.GlucoseChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.HeartRateChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.SmartBandChartActivity;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.utils.DateUtils;

import static br.edu.uepb.nutes.haniot.server.SynchronizationServer.context;

/**
 * Constructor of chart.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public final class CreateChart<T> {

    private LineData data;
    private BarData dataBar;
    private Params params;
    private Chart mChart;
    private List<Entry> entries;
    private List<BarEntry> entriesBar;
    private List<Entry> entries2;
    private List<T> dataList;
    private LineDataSet set;
    BarDataSet setBar;
    private LineDataSet set2;

    /**
     * @param params
     * @param mChart
     */
    public CreateChart(Params params, Chart mChart) {
        this.params = params;
        this.mChart = mChart;
    }

    /**
     * Configure and draw chart with List.
     *
     * @param dataList {@link List}
     */
    public void paint(List<T> dataList) {
        this.dataList = dataList;
        Paint p = mChart.getPaint(Chart.PAINT_INFO);
        p.setColor(Color.WHITE);
        p.setTextSize(32);
        mChart.setNoDataText(params.context.getString(R.string.no_data));
        configureDataChart();
        mChart.notifyDataSetChanged();

        LineChart lineChart = ((LineChart) mChart);
        //lineChart.setVisibleYRangeMaximum(50, YAxis.AxisDependency.LEFT);
        //lineChart.setVisibleYRangeMinimum(10.6f, YAxis.AxisDependency.LEFT);
        //lineChart.setVisibleXRangeMinimum(2f);
        lineChart.setVisibleXRangeMaximum(10f);
    }

    /**
     * Configure and draw chart with item.
     * Util for Real Time.
     *
     * @param item T
     */
    public void paint(T item) {

        if (dataList == null) {
            dataList = new ArrayList<T>();
            dataList.add(item);
            configureDataChart();
        }
        if (set == null) set = new LineDataSet(entries, params.legend[0]);
        if (item instanceof Measurement) {
            Measurement measurement = (Measurement) item;
            set.addEntry(new Entry((float) data.getEntryCount(), (float) measurement.getValue()));
            set.notifyDataSetChanged();
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();

            LineChart lineChart = ((LineChart) mChart);
            lineChart.setVisibleXRangeMaximum(params.visibiltyXMax);
            lineChart.setVisibleXRangeMinimum(params.visibiltyXMin);
        }
    }


    public void paintBar(List<T> data, String dateFormat) {
        this.dataList = data;
        params.formatDate(dateFormat);
        configureDataChart();
        mChart.notifyDataSetChanged();
    }

    /**
     * Format data for chart.
     *
     * @return formatter
     */
    public IAxisValueFormatter prepareVariablesBarData() {
        entriesBar = new ArrayList<>();
        final String[] quarters = new String[dataList.size()];


        if (dataList.get(0) instanceof Measurement) {

        }
        List<Measurement> data = (List<Measurement>) dataList;

        for (int i = 0; i < data.size(); i++) {
            String date = DateUtils.formatDate(
                    data.get(i).getTimestamp(), params.formatDate);
            entriesBar.add(new BarEntry((float) i, (float) data.get(i).getValue()));
            quarters[i] = date;
        }

        IAxisValueFormatter formatter = ((value, axis) -> {
            if (value >= quarters.length || value < 0) return "";
            return quarters[(int) value];

        });

        return formatter;
    }

    /**
     * Format data for chart.
     *
     * @return formatter
     */
    public IAxisValueFormatter prepareVariablesLineData() {
        entries = new ArrayList<>();
        entries2 = new ArrayList<>();
        final String[] quarters = new String[dataList.size()];

        List<Measurement> measurements = (List<Measurement>) dataList;

        for (int i = 0; i < measurements.size(); i++) {
            String date = DateUtils.formatDate(
                    measurements.get(i).getTimestamp(), params.formatDate);
            entries.add(new Entry((float) i, (int) measurements.get(i).getValue()));

//            if (!measurements.get(i).getDataset().isEmpty())
//                if (measurements.get(i).getDataset().get(0).getTypeId() == MeasurementType.BLOOD_PRESSURE_DIASTOLIC)
//                    entries2.add(new Entry(i, (float) measurements.get(i).getMeasurementList().get(0).getValue()));

            quarters[i] = date;
        }
//        for (int i = 0; i < data.size(); i++) {
//
//            String date = DateUtils.formatDate(
//                    data.get(i).getRegistrationDate(),
//                    params.formatDate);
//
//                entries.add(new Entry((float) i, (int) data.get(i).getValue()));
//
//                if (!data.get(i).getMeasurementList().isEmpty())
//                    if (data.get(i).getMeasurementList().get(0).getTypeId() == MeasurementType.BLOOD_PRESSURE_DIASTOLIC)
//                        entries2.add(new Entry(i, (float) data.get(i).getMeasurementList().get(0).getValue()));
//            quarters[i] = date;
//        }

        //Format date
        IAxisValueFormatter formatter = ((value, axis) -> {
            if (value >= quarters.length || value < 0) return "";
            return quarters[(int) value];

        });
        return formatter;
    }

    public void configureDataChart() {

        if (dataList.isEmpty()) {
            dataList.clear();
            mChart.invalidate();
            mChart.clear();
            mChart.clearAllViewportJobs();
            mChart.setVisibility(View.VISIBLE);
            return;
        }
        if (params.context instanceof SmartBandChartActivity) {

            mChart.getXAxis().setValueFormatter(prepareVariablesBarData());

            ArrayList<IBarDataSet> dataSetsBar = new ArrayList<>();
            setBar = new BarDataSet(entriesBar, params.legend[0]);
            setBar.setValueFormatter(new ValueFormatter(params.context));
            dataSetsBar.add(setBar);

            configureDesignChart();
            dataBar = new BarData(dataSetsBar);
            mChart.setData(dataBar);
        } else {
            mChart.getXAxis().setValueFormatter(prepareVariablesLineData());

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            set = new LineDataSet(entries, params.legend[0]);

            dataSets.add(set);
            if (!entries2.isEmpty()) {
                set2 = new LineDataSet(entries2, params.legend[1]);
                dataSets.add(set2);
            }

            configureDesignChart();
            data = new LineData(dataSets);
            mChart.setData(data);

            ((LineChart) mChart).moveViewToX(set.getXMax());
        }
    }

    /**
     * Configure proprietes of chart.
     */
    public void configureDesignChart() {
        if (dataList == null) return;

        mChart.invalidate();

        //XAxis
        mChart.getXAxis().setTextColor(params.colorTextX);
        mChart.getXAxis().setTextColor(params.colorTextY);
        mChart.getXAxis().setPosition(params.xAxisPosition);
        mChart.getXAxis().setGridColor(params.colorGrid);
        mChart.getXAxis().setEnabled(params.xAxisEnabled);
        mChart.getXAxis().setDrawGridLines(params.drawGridLinesX);
        mChart.getXAxis().setDrawAxisLine(params.drawLineX);
        mChart.getXAxis().setGranularity(1f);

        //Legend
        mChart.getLegend().setTextColor(params.colorValuesText);
        mChart.getLegend().setEnabled(false);
        if (params.legend[0] != null) mChart.getLegend().setEnabled(true);

        //Marker
        String pattern;
        if (params.context instanceof SmartBandChartActivity
                || params.context instanceof HeartRateChartActivity
                || params.context instanceof BloodPresssureChartActivity
                || params.context instanceof GlucoseChartActivity)
            pattern = params.context.getString(R.string.format_number_integer);
        else
            pattern = params.context.getString(R.string.format_number_float);

        MarkerViewCustom marker = new MarkerViewCustom(params.context, R.layout.marker_view, pattern);
        marker.getTvContent().setTextColor(params.colorTextMarker);
        if (params.backgroundDrawableMarker != null)
            marker.getLayoutBackground().setBackground(params.backgroundDrawableMarker);
        marker.getTvContent().setTextColor(Color.WHITE);
        mChart.setMarker(marker);
        marker.setMinimumHeight(30);

        //Description
        Description description = new Description();
        description.setText(params.description);
        description.setTextColor(params.colorDescription);
        mChart.setDescription(description);
        mChart.getDescription().setEnabled(true);

        if (mChart instanceof BarChart) {
            BarChart barChart = (BarChart) mChart;

            barChart.setBackgroundColor(Color.RED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                barChart.setElevation((float) 5.0);
            }
            barChart.setSoundEffectsEnabled(true);
            barChart.setDoubleTapToZoomEnabled(true);

            setBar.setColor(Color.WHITE);
            setBar.setBarBorderColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            setBar.setValueTextColor(Color.WHITE);
            setBar.setHighLightColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            setBar.setDrawValues(false);

            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisRight().setDrawGridLines(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setEnabled(false);
            barChart.setDrawValueAboveBar(false);

        }

        //LineChart
        if (mChart instanceof LineChart) {

            if (set2 != null) {
                set2.setColor(params.lineColor);
                set2.setValueTextColor(params.colorValuesText);
                set2.setFillColor(params.lineColor);
                set2.setDrawValues(params.drawValues);
                set2.setDrawCircles(params.drawCirclesBorderEnabled);
                set2.setDrawCircleHole(params.drawCirclesHoleEnabled);
                set2.setHighlightEnabled(params.highlightEnabled);
                set2.setHighLightColor(params.colorHighLight);
                set2.setCircleColor(ContextCompat.getColor(context, R.color.colorOrange));
                set2.setCircleColorHole(ContextCompat.getColor(context, R.color.colorPrimary));
                set2.setValueTextColor(params.colorValuesText);
                set2.setCircleRadius(params.circleValueRadius);
                set2.setCircleHoleRadius(params.circleHoleValueRadius);
                set2.setLineWidth(params.lineWidth);
                set2.setHighlightLineWidth(params.highlightLineWidth);
                set2.setMode(params.typeLine);
                set2.setDrawFilled(params.filledLine);
                set2.setFillColor(params.lineFilledColor);
                set2.setColor(ContextCompat.getColor(context, R.color.colorOrange));
            }

            LineChart mLineChart = (LineChart) mChart;
            mLineChart.setDrawGridBackground(false);
            mLineChart.getAxisLeft().setEnabled(params.yAxisEnabled);
            mLineChart.getAxisLeft().setEnabled(true);
            mLineChart.setDrawGridBackground(params.drawGridBackground);
            mLineChart.getAxisLeft().setDrawGridLines(params.drawGridLinesY);
            mLineChart.getAxisLeft().setDrawAxisLine(params.drawLineY);
            mLineChart.setDrawGridBackground(params.drawGridBackground);
            mLineChart.setDrawBorders(params.drawBorder);
            mLineChart.setBorderColor(params.colorBorderGrid);
            mLineChart.setGridBackgroundColor(params.colorBorderGrid);
            mLineChart.getAxisLeft().setTextColor(params.colorTextY);
            mLineChart.getAxisRight().setEnabled(true);
            mLineChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);
            mLineChart.getAxisRight().setGridColor(Color.TRANSPARENT);
            mLineChart.getAxisRight().setTextColor(Color.TRANSPARENT);
            mLineChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
            mLineChart.getAxisLeft().setGridColor(Color.TRANSPARENT);
            mLineChart.getAxisLeft().setTextColor(Color.TRANSPARENT);
            mLineChart.setAutoScaleMinMaxEnabled(false);

            mLineChart.getAxisRight().setAxisMinimum(4f);
            mLineChart.getAxisRight().setAxisMaximum(3f);


            //Set Proprietes
            set.setColor(params.lineColor);
            set.setValueTextColor(params.colorValuesText);
            set.setFillColor(params.lineColor);
            set.setDrawValues(params.drawValues);
            set.setDrawCircles(params.drawCirclesBorderEnabled);
            set.setDrawCircleHole(params.drawCirclesHoleEnabled);
            set.setHighlightEnabled(params.highlightEnabled);
            set.setHighLightColor(params.colorHighLight);
            set.setCircleColor(params.colorCircleBorderValue);
            set.setCircleColorHole(params.colorCircleHoleValue);
            set.setValueTextColor(params.colorValuesText);
            set.setCircleRadius(params.circleValueRadius);
            set.setCircleHoleRadius(params.circleHoleValueRadius);
            set.setLineWidth(params.lineWidth);
            set.setHighlightLineWidth(params.highlightLineWidth);
            set.setMode(params.typeLine);
            set.setDrawFilled(params.filledLine);
            set.setFillColor(params.lineFilledColor);
            set.setColor(params.lineColor);

            //Limit
            YAxis leftAxis = ((LineChart) mChart).getAxisLeft();
            leftAxis.removeAllLimitLines();
            for (Limit limit : params.limits) {
                LimitLine ll1 = new LimitLine(limit.getValue(), limit.getName());
                ll1.setLineWidth(1.4f);
                ll1.enableDashedLine(3f, 10f, 1f);
                ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(10f);
                ll1.setTextColor(Color.WHITE);
                ll1.setLineColor(limit.getColor());

                leftAxis.addLimitLine(ll1);
            }

            //RangeY
            if (params.YMin > -1 && params.YMax > params.YMin) {
                leftAxis.setAxisMinValue(params.YMin);
                leftAxis.setAxisMaxValue(params.YMax);
            }
        }


        //General
        mChart.setNoDataTextColor(Color.WHITE);
        mChart.setBackgroundColor(params.colorBackground);
        mChart.setNoDataTextColor(Color.WHITE);
        mChart.animate();
        mChart.setEnabled(true);
        mChart.invalidate();
        mChart.animateX(200);
    }

    /**
     * Class Params.
     */
    public static class Params {
        private Chart mChart;
        private Context context;
        private List<Limit> limits;
        private int colorTextX;
        private int colorTextY;
        private int lineColor;
        private int colorBackground;
        private int colorDescription;
        private int colorBorderGrid;
        private int colorGrid;
        private int colorValuesText;
        private int colorCircleBorderValue;
        private int colorCircleHoleValue;
        private int colorHighLight;
        private Drawable backgroundDrawableMarker;
        private int colorTextMarker;
        private boolean xAxisEnabled;
        private boolean yAxisEnabled;
        private boolean drawGridBackground;
        private boolean drawBorder;
        private boolean drawValues;
        private boolean drawCirclesBorderEnabled;
        private boolean drawCirclesHoleEnabled;
        private boolean drawGridLinesX;
        private boolean drawGridLinesY;
        private boolean drawLineX;
        private boolean drawLineY;
        private boolean highlightEnabled;
        private float circleHoleValueRadius;
        private float circleValueRadius;
        private float lineWidth;
        private float highlightLineWidth;
        private String description;
        private String[] legend;
        private String formatDate;
        private XAxis.XAxisPosition xAxisPosition;
        private LineDataSet.Mode typeLine;
        private boolean filledLine;
        private int lineFilledColor;
        private float visibiltyXMax;
        private float visibiltyXMin;
        private float YMax;
        private float YMin;

        /**
         * Constructor.
         *
         * @param context {@link Context}
         * @param chart   {@link Chart}
         */
        public Params(Context context, Chart chart) {
            this.colorBackground = Color.TRANSPARENT;
            this.drawGridBackground = false;
            this.description = "";
            this.colorDescription = Color.WHITE;
            this.colorBorderGrid = Color.WHITE;
            this.colorGrid = Color.TRANSPARENT;
            this.drawBorder = false;
            this.drawValues = false;
            this.legend = new String[2];
            this.colorValuesText = Color.BLACK;
            this.colorCircleBorderValue = ContextCompat.getColor(context, R.color.colorPrimary);
            this.colorCircleHoleValue = ContextCompat.getColor(context, R.color.colorPrimary);
            this.colorHighLight = Color.YELLOW;
            this.drawCirclesBorderEnabled = true;
            this.drawCirclesHoleEnabled = true;
            this.circleValueRadius = 3.5f;
            this.circleHoleValueRadius = 1.5f;
            this.highlightEnabled = true;
            this.lineWidth = 2.5f;
            this.highlightLineWidth = 2.0f;
            this.drawGridLinesX = false;
            this.drawGridLinesY = false;
            this.drawLineX = false;
            this.drawLineY = false;
            this.xAxisPosition = XAxis.XAxisPosition.BOTTOM;
            this.formatDate = context.getString(R.string.date_format_month_day);
            this.backgroundDrawableMarker = null;
            this.colorTextMarker = Color.BLACK;
            this.lineColor = ContextCompat.getColor(context, R.color.colorAccent);
            this.context = context;
            this.mChart = chart;
            this.colorTextX = Color.BLACK;
            this.colorTextY = Color.BLACK;
            this.xAxisEnabled = true;
            this.yAxisEnabled = true;
            this.typeLine = LineDataSet.Mode.CUBIC_BEZIER;
            this.filledLine = false;
            this.lineFilledColor = lineColor;
            this.visibiltyXMax = 40;
            this.visibiltyXMin = 10;
            this.limits = new ArrayList<>();
            this.YMin = -1;
            this.YMax = -1;
        }

        /**
         * Sets the of limit.
         *
         * @params limit, valueLimit
         */
        public Params createLimit(String limit, float valueLimit, int colorLimit) {
            limits.add(new Limit(limit, valueLimit, colorLimit));
            return this;
        }

        /**
         * Sets the format date.
         *
         * @param formatDate
         */
        public Params formatDate(String formatDate) {
            this.formatDate = formatDate;
            return this;
        }

        /**
         * Enable/Disable filled line and color.
         *
         * @param filledLine
         * @return
         */
        public Params setStyleFilledLine(boolean filledLine, int lineFilledColor) {
            this.filledLine = filledLine;
            this.lineFilledColor = lineFilledColor;
            return this;
        }

        /**
         * Sets the format date.
         *
         * @param backgroundDrawableMarker {@link Drawable}
         * @param colorText
         * @return {@link Params}
         */
        public Params setStyleMarker(Drawable backgroundDrawableMarker, int colorText) {
            this.backgroundDrawableMarker = backgroundDrawableMarker;
            this.colorTextMarker = colorText;
            return this;
        }

        /**
         * set type of line.
         *
         * @param typeLine
         * @return
         */
        public Params setTypeLine(LineDataSet.Mode typeLine) {
            this.typeLine = typeLine;
            return this;
        }

        /**
         * set Max/Min visibility of X.
         *
         * @param visibiltyXMax
         * @param visibiltyXMin
         * @return
         */
        public Params setMaxVisibility(float visibiltyXMax, float visibiltyXMin) {
            this.visibiltyXMax = visibiltyXMax;
            this.visibiltyXMin = visibiltyXMin;
            return this;
        }

        /**
         * set range of Y.
         *
         * @param YMax
         * @param YMin
         * @return
         */
        public Params setRangeY(float YMax, float YMin) {
            this.YMax = YMax;
            this.YMin = YMin;
            return this;
        }

        /**
         * Set style circles.
         *
         * @param colorCircleBorderValue
         * @param colorCircleHoleValue
         * @return {@link Params}
         */
        public Params drawCircleStyle(int colorCircleBorderValue, int colorCircleHoleValue) {
            this.colorCircleBorderValue = colorCircleBorderValue;
            this.colorCircleHoleValue = colorCircleHoleValue;
            this.drawCirclesBorderEnabled = colorCircleBorderValue == 0 ? false : true;
            this.drawCirclesHoleEnabled = colorCircleHoleValue == 0 ? false : true;
            return this;
        }

        /**
         * Define circles size.
         *
         * @param circleValueRadius
         * @param circleHoleValueRadius
         * @return {@link Params}
         */
        public Params drawCircleRadius(float circleValueRadius, float circleHoleValueRadius) {
            this.circleValueRadius = circleValueRadius;
            this.circleHoleValueRadius = circleHoleValueRadius;
            return this;
        }

        /**
         * Enable/Disable highlight in chart.
         *
         * @param highlightEnabled
         * @return {@link Params}
         */
        public Params highlightEnabled(boolean highlightEnabled) {
            this.highlightEnabled = highlightEnabled;
            return this;
        }

        /**
         * Set width of line of chart.
         *
         * @param colorHighLight
         * @param highlightLineWidth
         * @return {@link Params}
         */
        public Params highlightStyle(int colorHighLight, float highlightLineWidth) {
            this.colorHighLight = colorHighLight;
            this.highlightLineWidth = highlightLineWidth;
            return this;
        }

        /**
         * Show/Hide line of x.
         *
         * @param drawLineX
         * @param drawLineY
         * @return {@link Params}
         */
        public Params drawLinesAxis(boolean drawLineX, boolean drawLineY) {
            this.drawLineX = drawLineX;
            this.drawLineY = drawLineY;
            return this;
        }

        /**
         * Show/Hide grid line of X.
         *
         * @param drawGridLineX
         * @param drawGridLineY
         * @return {@link Params}
         */
        public Params drawGrid(boolean drawGridLineX, boolean drawGridLineY) {
            this.drawGridLinesX = drawGridLineX;
            this.drawGridLinesY = drawGridLineY;
            return this;
        }

        /**
         * Set size of line chart.
         *
         * @param lineWidth
         * @param color
         * @return {@link Params}
         */
        public Params lineStyle(float lineWidth, int color) {
            this.lineWidth = lineWidth;
            this.lineColor = color;
            return this;
        }

        /**
         * set position of xAxis.
         *
         * @param colorTextX
         * @param xAxisPosition
         * @return {@link Params}
         */
        public Params xAxisStyle(int colorTextX, XAxis.XAxisPosition xAxisPosition) {
            this.colorTextX = colorTextX;
            this.xAxisPosition = xAxisPosition;
            return this;
        }

        /**
         * Set style of yAxis.
         *
         * @param colorTextY
         * @return {@link Params}
         */
        public Params yAxisStyle(int colorTextY) {
            this.colorTextY = colorTextY;
            return this;
        }

        /**
         * Enable/Disable xAxis.
         *
         * @param enabled
         * @return {@link Params}
         */
        public Params xAxisEnabled(boolean enabled) {
            this.xAxisEnabled = enabled;
            return this;
        }

        /**
         * Enable/Disable yAxis.
         *
         * @param enabled
         * @return {@link Params}
         */
        public Params yAxisEnabled(boolean enabled) {
            this.yAxisEnabled = enabled;
            return this;
        }

        /**
         * Set color values.
         *
         * @param colorValuesText
         * @return {@link Params}
         */
        public Params setTextValuesColor(int colorValuesText) {
            this.colorValuesText = colorValuesText;
            return this;
        }

        /**
         * add legend of chart.
         *
         * @param legends
         * @return {@link Params}
         */
        public Params addLegend(@Nullable String... legends) {
            this.legend[0] = legends[0];

            if (legends.length > 1) this.legend[1] = legends[1];
            return this;
        }


        /**
         * set color grid of chart.
         */
        public Params colorGridChart(int colorBorderGrid, int colorGridBackground) {
            this.colorBorderGrid = colorBorderGrid;
            this.colorGrid = colorGridBackground;
            return this;
        }

        /**
         * set color grid of chart.
         *
         * @param colorBackground
         * @return {@link Params}
         */
        public Params colorChart(int colorBackground) {
            this.colorBackground = colorBackground;
            return this;
        }

        /**
         * set color font of chart.
         *
         * @param color
         */
        public Params colorFontDescription(int color) {
            colorDescription = color;
            return this;
        }

        /**
         * Enable/Disable grid background of chart.
         *
         * @param drawGridBackground
         */
        public Params gridBackground(boolean drawGridBackground) {
            this.drawGridBackground = drawGridBackground;
            return this;
        }

        /**
         * Enable/Disable borders of chart.
         *
         * @param drawBorder
         */
        public Params drawBorders(boolean drawBorder) {
            this.drawBorder = drawBorder;
            return this;
        }

        /**
         * Enable/Disable values of chart.
         *
         * @param drawValues
         */
        public Params drawValues(boolean drawValues) {
            this.drawValues = drawValues;
            return this;
        }

        /**
         * Configure and generate graph.
         */
        public CreateChart build() {
            return new CreateChart(this, mChart);
        }
    }
}
