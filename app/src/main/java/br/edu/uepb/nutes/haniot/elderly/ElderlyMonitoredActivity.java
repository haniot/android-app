package br.edu.uepb.nutes.haniot.elderly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.ElderlyMonitoredAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.Elderly;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ElderlyMonitoredActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyMonitoredActivity extends AppCompatActivity implements OnRecyclerViewListener<Elderly> {
    private final String TAG = "PatientsMonitoredFrag";
    private final int LIMIT_PER_PAGE = 20;

    private ElderlyMonitoredAdapter mAdapter;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;
    private Params params;
    private Session session;
    private Server server;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.no_data_textView)
    TextView mNoDataTextView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.patients_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.patient_add_floating_button)
    FloatingActionButton mAddPatientButton;

    public ElderlyMonitoredActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_monitored);
        ButterKnife.bind(this);

        session = new Session(this);
        server = Server.getInstance(this);
        params = new Params(session.get_idLogged(), MeasurementType.TEMPERATURE);

        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initComponents() {
        initToolBar();
        initRecyclerView();
        initDataSwipeRefresh();

        mAddPatientButton.setOnClickListener((v) -> {
//            startActivity(new Intent(getApplicationContext(), ElderlyRegisterActivity.class));
            startActivity(new Intent(getApplicationContext(), FallCharacterizationActivity.class));
            server.cancelAllResquest();
        });
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.elderly_monitored));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        mAdapter = new ElderlyMonitoredAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (itShouldLoadMore) loadData();
            }
        });
    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadData() {
        mAdapter.clearItems(); // clear list

        toggleLoading(true); // Enable loading
        if (ConnectionUtils.internetIsEnabled(this)) {
            String path = "/users/".concat(session.get_idLogged()).concat("/external");
            server.get(path, new Server.Callback() {
                @Override
                public void onError(JSONObject result) {
                    Log.w(TAG, "loadData - onError()");
                    toggleLoading(false); // Disable loading
                    if (mAdapter.itemsIsEmpty()) printMessage(getString(R.string.error_500));
                }

                @Override
                public void onSuccess(JSONObject result) {
                    toggleLoading(false); // Disable loading

                    if (result != null && result.length() > 0) {
                        mAdapter.addItems(transform(result));
                    }
                    toggleNoDataMessage(true); // Enable message no data
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        server.cancelAllResquest();
    }

    // TODO IMPLEMENTAR NO MODULO HISTORICAL
    private List<Elderly> transform(JSONObject json) {
        List<Elderly> result = new ArrayList<>();
        try {
            JSONArray arrayData = json.getJSONArray("externalData");

            for (int i = 0; i < arrayData.length(); i++) {
                JSONObject o = arrayData.getJSONObject(i);

                Elderly e = new Elderly();
                e.setName(o.getString("name"));
                e.setFallRisk(o.getInt("fallRisk"));
                result.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     */
    private void toggleLoading(boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!enabled) {
                    mDataSwipeRefresh.setRefreshing(false);
                    itShouldLoadMore = true;
                } else {
                    mDataSwipeRefresh.setRefreshing(true);
                    itShouldLoadMore = false;
                }
            }
        });
    }

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    private void toggleNoDataMessage(boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    if (!ConnectionUtils.internetIsEnabled(getApplicationContext())) {
                        mNoDataTextView.setText(getString(R.string.connect_network_try_again));
                    } else {
                        mNoDataTextView.setText(getString(R.string.no_data_available));
                    }
                    mNoDataTextView.setVisibility(View.VISIBLE);
                } else {
                    mNoDataTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Print Toast Messages.
     *
     * @param message
     */
    private void printMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
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
    public void onItemClick(Elderly item) {
        Log.w(TAG, "onItemClick()");
        startActivity(new Intent(getApplicationContext(), FallRiskActivity.class));
    }
}
