package br.edu.uepb.nutes.haniot.activity.graphs;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.dao.ContextMeasurementDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;

import static br.edu.uepb.nutes.haniot.server.SynchronizationServer.context;

/**
 * Created by izabella on 01/11/17.
 */

public class TemperatureGraphActivity extends AppCompatActivity implements View.OnClickListener {
    private final int GRAPH_TYPE_DAY = 1;
    private final int GRAPH_TYPE_SEVEN = 2;
    private final int GRAPH_TYPE_MONTH = 3;

    private Session session;

    List<Measurement> measurementData;

    @BindView(R.id.chart_temperature)
    LineChart mChart;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.ver_por_mes)
    Button mButtonMonth;

    @BindView(R.id.ver_por_semana)
    Button mButtonSeven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_graph);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Temperature");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(this);

        mButtonMonth.setOnClickListener(this);
        mButtonSeven.setOnClickListener(this);

        createChart(GRAPH_TYPE_DAY);
    }

    private void createChart(int type) {

        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        final SynchronizationServer.Callback callbackSynchronization = null;
        String jsonMea0surements;

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);

        mChart.getAxisRight().setEnabled(false);
        long dayMile = (24 * 60 * 60 * 1000);
        long dateEnd = DateUtils.getCurrentDatetime();
        long dateStart = dateEnd - (dayMile * 7);
        String deviceAddress = "1C:87:74:01:73:10";

        if (type == GRAPH_TYPE_DAY) {
            long userId = session.getUserLogged().getId();

            Server.getInstance(this).get("/measurements/types/1?dateStart=2018-01-08&period=7" + new Server.Callback() {
                @Override
                public void onError(JSONObject result) {
                    if (callbackSynchronization != null) callbackSynchronization.onError(result);
                }

                @Override
                public void onSuccess(JSONObject result) {
                    // popular a lista aqui
                    if (callbackSynchronization != null) callbackSynchronization.onSuccess(result);
                }

            });

        }

    }

    @Override
    public void onClick(View v) {

    }
}