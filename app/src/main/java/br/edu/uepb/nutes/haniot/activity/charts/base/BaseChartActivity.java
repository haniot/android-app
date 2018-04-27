package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import butterknife.BindView;

/**
 * Base Chart implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
abstract public class BaseChartActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "FragmentActivity";

    public final int GRAPH_TYPE_DAY = 1;
    public final int GRAPH_TYPE_SEVEN = 2;
    public final int GRAPH_TYPE_MONTH = 3;

    public Session session;
    public Params params;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.day_button)
    public Button mButtonDay;

    @BindView(R.id.week_button)
    public Button mButtonWeek;

    @BindView(R.id.month_button)
    public Button mButtonMonth;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.day_button:
                requestData(GRAPH_TYPE_DAY);
                break;

            case R.id.month_button:
                requestData(GRAPH_TYPE_MONTH);
                break;

            case R.id.week_button:
                requestData(GRAPH_TYPE_SEVEN);
                break;
        }
    }

    private void requestDataInServer(String period) {
        Historical hist = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER) // required
                .params(params) // required
                .filterDate(period)
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
                if (result != null && result.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onUpdateData(result);
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
        if (type == GRAPH_TYPE_DAY)
            requestDataInServer("1d");
        else if (type == GRAPH_TYPE_SEVEN)
            requestDataInServer("1w");
        else if (type == GRAPH_TYPE_MONTH)
            requestDataInServer("1m");
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

    abstract public void onUpdateData(List<Measurement> data);
}
