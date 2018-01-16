//package br.edu.uepb.nutes.haniot.activity.graphs;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.AxisBase;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.IAxisValueFormatter;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import br.edu.uepb.nutes.haniot.R;
//import br.edu.uepb.nutes.haniot.activity.settings.Session;
//import br.edu.uepb.nutes.haniot.model.Measurement;
//import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
//import br.edu.uepb.nutes.haniot.server.Server;
//import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
//import br.edu.uepb.nutes.haniot.utils.DateUtils;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by izabella on 23/10/17.
// */
//
//public class ScaleGraphActivity extends AppCompatActivity implements View.OnClickListener{
//    private final int GRAPH_TYPE_DAY = 1;
//    private final int GRAPH_TYPE_SEVEN = 2;
//    private final int GRAPH_TYPE_MONTH = 3;
//
//    private Session session;
//
//    List<Measurement> measurementData;
//
//    @BindView(R.id.chart_scale)
//    LineChart mChart;
//
//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;
//
//    @BindView(R.id.ver_por_mes)
//    Button mButtonMonth;
//
//    @BindView(R.id.ver_por_semana)
//    Button mButtonSeven;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scale_graph);
//        ButterKnife.bind(this);
//
//
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Weight");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        session = new Session(this);
//
//        mButtonMonth.setOnClickListener(this);
//        mButtonSeven.setOnClickListener(this);
//
//        createChart(GRAPH_TYPE_DAY);
//    }
//
//    private void createChart(int type) {
//
//        mChart.getDescription().setEnabled(false);
//        mChart.setDrawGridBackground(false);
//        final SynchronizationServer.Callback callbackSynchronization = null;
//        String jsonMea0surements;
//
//        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.setAxisMaximum(100f);
//
//        mChart.getAxisRight().setEnabled(false);
//        long userId = session.getUserLogged().getId();
//
//
//        if (type == GRAPH_TYPE_DAY) {
//
//            Server.getInstance(this).get("/measurements/types/3?period/1d" + new Server.Callback() {
//                @Override
//                public void onError(JSONObject result) {
//                    if (callbackSynchronization != null) callbackSynchronization.onError(result);
//                }
//
//                @Override
//                public void onSuccess(JSONObject result) {
//                    // popular a lista aqui
//                    if (callbackSynchronization != null) callbackSynchronization.onSuccess(result);
//                }
//
//            });
//
//
////            final String[] quarters = new String[measurementData.size()];
////            ArrayList<Entry> entries = new ArrayList<Entry>();
////
////            for(int i = 0; i < measurementData.size(); i++) {
////                String format = "HH:MM:SS";
////                String date =  DateUtils.getDatetime(measurementData.get(i).getRegistrationTime(), format);
////                float weight = measurementData.get(i).getWeight();
////                entries.add(new Entry((float)i, weight));
////                quarters[i] = date;
////            }
////            IAxisValueFormatter formatter = new IAxisValueFormatter() {
////
////                @Override
////                public String getFormattedValue(float value, AxisBase axis) {
////                    if(value >= quarters.length){return "";}
////                    return quarters[(int) value];
////                }
////
////                // we don't draw numbers, so no decimal digits needed
////                public int getDecimalDigits() {  return 0; }
////            };
////            XAxis xAxis = mChart.getXAxis();
////            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
////            xAxis.setValueFormatter(formatter);
////            xAxis.setEnabled(true);
////            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
////
////            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
////
////            LineDataSet set = new LineDataSet(entries, "weight");
////            set.setLineWidth(3f);
////            set.setDrawCircles(true);
////            set.setDrawCircleHole(true);
////            set.setAxisDependency(YAxis.AxisDependency.LEFT);
////            dataSets.add(set);
////            LineData data = new LineData(dataSets);
////            mChart.animate();
////            mChart.setData(data);
////            mChart.setEnabled(true);
////            mChart.invalidate();
////            mChart.setVisibleXRangeMaximum(65f);
////            mChart.resetViewPortOffsets();
////            mChart.animateX(3000);
////            mChart.notifyDataSetChanged();
////        }else if(type == GRAPH_TYPE_SEVEN) {
//
//        }
//        else if (type == GRAPH_TYPE_SEVEN){ //semana atual
//
//            Server.getInstance(this).get("/measurements/types/3?period/1w" + new Server.Callback() {
//                @Override
//                public void onError(JSONObject result) {
//                    if (callbackSynchronization != null) callbackSynchronization.onError(result);
//                }
//
//                @Override
//                public void onSuccess(JSONObject result) {
//                    // popular a lista aqui
//                    if (callbackSynchronization != null) callbackSynchronization.onSuccess(result);
//                }
//
//            });
//        }
//
//        else if (type == GRAPH_TYPE_MONTH){ //mês atual
//
//            Server.getInstance(this).get("/measurements/types/3?period/1m" + new Server.Callback() {
//                @Override
//                public void onError(JSONObject result) {
//                    if (callbackSynchronization != null) callbackSynchronization.onError(result);
//                }
//
//                @Override
//                public void onSuccess(JSONObject result) {
//                    // popular a lista aqui
//                    if (callbackSynchronization != null) callbackSynchronization.onSuccess(result);
//                }
//
//            });
//        }
//    }
//
//    private long getDateTime(int millis) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(c.getTimeInMillis() + millis);
//
//        return c.getTimeInMillis();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                super.onBackPressed();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ver_por_mes:
//                createChart(GRAPH_TYPE_MONTH);
//                break;
//
//            case R.id.ver_por_semana:
//                createChart(GRAPH_TYPE_SEVEN);
//                break;
//        }
//    }
//}