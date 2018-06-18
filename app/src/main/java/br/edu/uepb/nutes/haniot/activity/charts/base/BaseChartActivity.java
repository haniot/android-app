package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.NameColumnsDB;
import butterknife.BindView;

/**
 * Base Chart implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
abstract public class BaseChartActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "ChartActivity";

    public final int CHART_TYPE_DAY = 1;
    public final int CHART_TYPE_SEVEN = 2;
    public final int CHART_TYPE_MONTH = 3;
    public final int CHART_TYPE_YEAR = 4;

    private int currentChartType;
    public Session session;
    public Params params;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.fab_year)
    public FloatingActionButton fabYear;

    @BindView(R.id.fab_month)
    public FloatingActionButton fabMonth;

    @BindView(R.id.fab_week)
    public FloatingActionButton fabWeek;

    @BindView(R.id.fab_day)
    public FloatingActionButton fabDay;

    @BindView(R.id.menu_period)
    public FloatingActionMenu fabActionMenu;

    public BaseChartActivity() {
        this.currentChartType = CHART_TYPE_MONTH;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_day:
                requestData(CHART_TYPE_DAY);
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_day));
                fabActionMenu.close(true);
                break;

            case R.id.fab_month:
                requestData(CHART_TYPE_MONTH);
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_month));
                fabActionMenu.close(true);
                break;

            case R.id.fab_week:
                requestData(CHART_TYPE_SEVEN);
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_week));
                fabActionMenu.close(true);
                break;
            case R.id.fab_year:
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_year));
                requestData(CHART_TYPE_YEAR);
                fabActionMenu.close(true);
                break;
        }
    }

    private void requestDataInServer(String period) {
        Historical hist = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params)
                .filterDate(period)
                .ordination(NameColumnsDB.MEASUREMENT_REGISTRATION_DATE, "asc")
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
                if (result.isEmpty() && result != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onUpdateData(new ArrayList<>(), currentChartType);
                            createMoreInfo(new ArrayList<>());
                        }
                    });
                }
                if (result != null && result.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onUpdateData(result, currentChartType);
                            createMoreInfo(result);
                        }
                    });
                }
            }

            @Override
            public void onAfterSend() {
                Log.w(TAG, "onAfterSend()");
                // TODO Remover loading
            }
        });
    }

    /**
     * Request data in server.
     *
     * @param type
     */
    protected void requestData(int type) {
        if (type == CHART_TYPE_DAY) {
            currentChartType = CHART_TYPE_DAY;
            requestDataInServer("1d");
        } else if (type == CHART_TYPE_SEVEN) {
            currentChartType = CHART_TYPE_SEVEN;
            requestDataInServer("1w");
        } else if (type == CHART_TYPE_MONTH) {
            currentChartType = CHART_TYPE_MONTH;
            requestDataInServer("1m");
        } else if (type == CHART_TYPE_YEAR) {
            currentChartType = CHART_TYPE_YEAR;
            requestDataInServer("1y");
        }
    }

    /**
     * Display message.
     *
     * @param message
     */
    private void printMessage(String message) {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        });
    }


    public void createMoreInfo(List<Measurement> measurements) {

        ArrayList<InfoMeasurement> infoMeasurements = new ArrayList<>();
        infoMeasurements.addAll(getInfosBase(measurements));

        GridView gridView = (GridView) findViewById(R.id.moreinfo_grid);
        InfoAdapter infoAdapter = new InfoAdapter(this, infoMeasurements);
        gridView.setAdapter(infoAdapter);
    }

    private ArrayList<InfoMeasurement> getInfosBase(List<Measurement> measurements) {

        ArrayList<InfoMeasurement> infos = new ArrayList<>();

        if (measurements.isEmpty()) {
            infos.add(new InfoMeasurement(getString(R.string.info_max), "-"));
            infos.add(new InfoMeasurement(getString(R.string.info_min), "-"));
            infos.add(new InfoMeasurement(getString(R.string.info_avarage), "-"));
            infos.add(new InfoMeasurement(getString(R.string.info_period), " - "));
        } else {
            double measurementValueMax = measurements.get(0).getValue();
            double measurementValueMin = measurements.get(0).getValue();
            double measurementValueAvarage = 0.0;

            for (Measurement measurement : measurements) {
                measurementValueAvarage += measurement.getValue();
                if (measurementValueMax < measurement.getValue()) {
                    measurementValueMax = measurement.getValue();
                }
                if (measurementValueMin > measurement.getValue()) {
                    measurementValueMin = measurement.getValue();
                }

            }
            String unit = " " + measurements.get(0).getUnit();
            String firstMeasurement = DateUtils.formatDate(measurements.get(0).getRegistrationDate(), getString(R.string.date_format));
            String lastMeasurement = DateUtils.formatDate(measurements.get(measurements.size() - 1).getRegistrationDate(), getString(R.string.date_format));

            infos.add(new InfoMeasurement(getString(R.string.info_max), (String.format("%.1f", measurementValueMax)) + unit));
            infos.add(new InfoMeasurement(getString(R.string.info_min), (String.format("%.1f", measurementValueMin)) + unit));
            infos.add(new InfoMeasurement(getString(R.string.info_avarage), (String.format("%.1f", measurementValueAvarage)) + unit));
            if (firstMeasurement.equals(lastMeasurement))
                infos.add(new InfoMeasurement(getString(R.string.info_period), firstMeasurement));
            else
                infos.add(new InfoMeasurement(getString(R.string.info_period), firstMeasurement + "\n-\n" + lastMeasurement));
        }

        return infos;
    }


    abstract public void onUpdateData(List<Measurement> data, int currentChartType);

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}


