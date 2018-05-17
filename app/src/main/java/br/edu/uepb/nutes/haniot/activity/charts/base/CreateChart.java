package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.DateUtils;

/**
 * Constructor of chart.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public final class CreateChart<T> {

    private LineData data;
    private Params params;
    private Chart mChart;
    private List<Entry> entries;
    private List<T> dataList;
    private LineDataSet set;

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
        mChart.setNoDataText(params.context.getString(R.string.noData));
        configureDataChart();
        mChart.notifyDataSetChanged();
    }

    /**
     * Configure and draw chart with item.
     *
     * @param item T
     */
    public void paint(T item) {

        if (dataList == null) {
            dataList = new ArrayList<T>();
            dataList.add(item);
            //mChart.setEnabled(true);
            configureDataChart();
        }
        if (set == null) set = new LineDataSet(entries, params.label);
        if (item instanceof Measurement) {
            Measurement measurement = (Measurement) item;
            set.addEntry(new Entry((float) data.getEntryCount(), (float) measurement.getValue()));
            set.notifyDataSetChanged();
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            ((LineChart) mChart).setVisibleXRangeMaximum(params.visibiltyXMax);
            ((LineChart) mChart).setVisibleXRangeMinimum(params.visibiltyXMin);
            ((LineChart) mChart).moveViewToX(data.getXMax());
        }
    }

    /**
     * Format data for chart.
     *
     * @return formatter
     */
    public IAxisValueFormatter prepareVariablesLineData() {
        entries = new ArrayList<>();
        final String[] quarters = new String[dataList.size()];

        if (dataList.get(0) instanceof Measurement) {
            List<Measurement> data = (List<Measurement>) dataList;

            for (int i = 0; i < data.size(); i++) {
                String date = DateUtils.formatDate(
                        data.get(i).getRegistrationDate(),
                        params.formatDate);

                float valueMeasurement = (float) data.get(i).getValue();
                entries.add(new Entry((float) i, valueMeasurement));
                quarters[i] = date;
            }
        }

        IAxisValueFormatter formatter = ((value, axis) -> {
            if (value >= quarters.length || value < 0) return "";
            return quarters[(int) value];
        });

        return formatter;
    }

    public void configureDataChart(){

        if (dataList.isEmpty()) {
            dataList.clear();
            mChart.invalidate();
            mChart.clear();
            mChart.clearAllViewportJobs();
            mChart.setVisibility(View.VISIBLE);
            return;
        }

        mChart.getXAxis().setValueFormatter(prepareVariablesLineData());
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        set = new LineDataSet(entries, params.label);
        configureDesignChart();
        dataSets.add(set);
        data = new LineData(dataSets);
        mChart.setData(data);
    }

    /**
     * Configure proprietes of chart.
     */
    public void configureDesignChart() {
        if (dataList == null) return;

        mChart.invalidate();
        mChart.getXAxis().setTextColor(params.colorTextX);
        mChart.getXAxis().setTextColor(params.colorTextY);
        mChart.getXAxis().setPosition(params.xAxisPosition);
        mChart.getXAxis().setGridColor(params.colorGrid);
        mChart.getXAxis().setEnabled(params.xAxisEnabled);
        mChart.getLegend().setTextColor(params.colorLegend);
        mChart.getLegend().setEnabled(false);
        if (!params.label.isEmpty()) mChart.getLegend().setEnabled(true);

        MarkerViewCustom marker = new MarkerViewCustom(params.context, R.layout.marker_view);
        marker.getTvContent().setTextColor(params.colorTextMarker);
        if (params.backgroundDrawableMarker != null)
            marker.getLayoutBackground().setBackground(params.backgroundDrawableMarker);
        marker.getTvContent().setTextColor(Color.WHITE);



        marker.setMinimumHeight(30);
        mChart.getLegend().setTextColor(params.colorLegend);
        mChart.setMarker(marker);

        mChart.setNoDataTextColor(Color.WHITE);
        Description description = new Description();
        description.setText(params.description);
        description.setTextColor(params.colorDescription);
        mChart.setDescription(description);

        //Configure chart
        mChart.getDescription().setEnabled(true);
        mChart.setBackgroundColor(params.colorBackground);
        mChart.getXAxis().setDrawGridLines(params.drawGridLinesX);
        mChart.getXAxis().setDrawAxisLine(params.drawLineX);

        set.setColor(params.lineColor);
        set.setValueTextColor(params.texColor);
        set.setFillColor(params.texColor);
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
        mChart.setNoDataTextColor(Color.WHITE);
        LineChart mLineChart2 = (LineChart) mChart;
        mLineChart2.getAxisLeft().setEnabled(params.yAxisEnabled);

        ((LineChart) mChart).setDrawGridBackground(false);

        if (mChart instanceof LineChart) {
            LineChart mLineChart = (LineChart) mChart;
            mLineChart.getAxisLeft().setEnabled(params.yAxisEnabled);
            mLineChart.setDrawGridBackground(params.drawGridBackground);
            mLineChart.getAxisLeft().setDrawGridLines(params.drawGridLinesY);
            mLineChart.getAxisLeft().setDrawAxisLine(params.drawLineY);
            mLineChart.setDrawGridBackground(params.drawGridBackground);
            mLineChart.setDrawBorders(params.drawBorder);
            mLineChart.setBorderColor(params.colorBorderGrid);
            mLineChart.setGridBackgroundColor(params.colorBorderGrid);
            mLineChart.getAxisLeft().setTextColor(params.colorTextY);
            mLineChart.getAxisRight().setEnabled(false);

            mLineChart.getAxisRight().setAxisMinimum(10f);
            mLineChart.getAxisRight().setAxisMaximum(12f);
            if (data == null)((LineChart) mChart).zoom(1.1f, 1f,0, 0);
            // limit the number of visible entries
            mLineChart.setPinchZoom(true);
        }

        set.setMode(params.typeLine);
        set.setDrawFilled(params.filledLine);
        set.setFillColor(params.lineFilledColor);

        ((LineChart) mChart).setAutoScaleMinMaxEnabled(false);
        set.setColor(params.lineColor);
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
        private int colorTextX;
        private int colorTextY;
        private int lineColor;
        private int colorBackground;
        private int colorDescription;
        private int colorBorderGrid;
        private int colorGrid;
        private int texColor;
        private int colorValuesText;
        private int colorCircleBorderValue;
        private int colorCircleHoleValue;
        private int colorHighLight;
        private int colorLegend;
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
        private String label;
        private String formatDate;
        private XAxis.XAxisPosition xAxisPosition;
        private LineDataSet.Mode typeLine;
        private boolean filledLine;
        private int lineFilledColor;
        private float visibiltyXMax;
        private float visibiltyXMin;

        /**
         * Constructor.
         *
         * @param context {@link Context}
         * @param chart {@link Chart}
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
            this.label = "";
            this.texColor = Color.BLACK;
            this.colorValuesText = Color.BLACK;
            this.colorCircleBorderValue = ContextCompat.getColor(context, R.color.colorPrimary);
            this.colorCircleHoleValue = ContextCompat.getColor(context, R.color.colorPrimary);
            this.colorHighLight = Color.YELLOW;
            this.drawCirclesBorderEnabled = true;
            this.drawCirclesHoleEnabled = true;
            this.circleValueRadius = 4.0f;
            this.circleHoleValueRadius = 3.0f;
            this.highlightEnabled = true;
            this.lineWidth = 2.5f;
            this.highlightLineWidth = 2.0f;
            this.drawGridLinesX = false;
            this.drawGridLinesY = false;
            this.drawLineX = false;
            this.drawLineY = false;
            this.xAxisPosition = XAxis.XAxisPosition.BOTTOM;
            this.formatDate = "dd/MMMM";
            this.backgroundDrawableMarker = null;
            this.colorTextMarker = Color.BLACK;
            this.lineColor = ContextCompat.getColor(context, R.color.colorAccent);
            this.context = context;
            this.mChart = chart;
            this.colorTextX = Color.BLACK;
            this.colorTextY = Color.BLACK;
            this.xAxisEnabled = true;
            this.yAxisEnabled = true;
            this.colorLegend = Color.BLACK;
            this.typeLine = LineDataSet.Mode.CUBIC_BEZIER;
            this.filledLine = false;
            this.lineFilledColor = lineColor;
            this.visibiltyXMax = 20;
            this.visibiltyXMin = 20;

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
         * @param filledLine
         * @return
         */
        public Params setStyleFilledLine(boolean filledLine, int lineFilledColor){
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
         * @param typeLine
         * @return
         */
        public Params setTypeLine(LineDataSet.Mode typeLine){
            this.typeLine = typeLine;
            return this;
        }

        /**
         *
         * set Max/Min visibility of X.
         * @param visibiltyXMax
         * @param visibiltyXMin
         * @return
         */
        public Params setMaxVisibility(float visibiltyXMax, float visibiltyXMin){
            this.visibiltyXMax = visibiltyXMax;
            this.visibiltyXMin = visibiltyXMin;
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
         * Set text color lengend and description.
         *
         * @param texColor
         * @return {@link Params}
         */
        public Params setTextColor(int texColor) {
            this.texColor = texColor;
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
         * set label of chart.
         *
         * @param label
         * @param color
         * @return {@link Params}
         */
        public Params setLabel(@Nullable String label, int color) {
            this.colorLegend = color;
            this.label = label == null ? "" : label;
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
        public Params colorFont(int color) {
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
