package br.edu.uepb.nutes.haniot.elderly;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.ElderlyMonitoredAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.dao.ElderlyDAO;
import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ElderlyMonitoredActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class ElderlyMonitoredActivity extends AppCompatActivity implements OnRecyclerViewListener<Elderly> {
    private final String TAG = "ElderlyMonitoredAc";
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
    private ElderlyDAO elderlyDAO;
    private boolean itemClicked;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.no_data_textView)
    TextView mNoDataTextView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.elderlies_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.elderly_add_floating_button)
    FloatingActionButton mAddElderlyButton;

    public ElderlyMonitoredActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_monitored);
        ButterKnife.bind(this);

        session = new Session(this);
        server = Server.getInstance(this);
        elderlyDAO = ElderlyDAO.getInstance(this);
        params = new Params(session.get_idLogged());

        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemClicked = false;
        loadData();
    }

    private void initComponents() {
        initToolBar();
        initRecyclerView();
        initDataSwipeRefresh();

        mAddElderlyButton.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), ElderlyRegisterActivity.class));
            server.cancelTagRequest(this.getClass().getName());
        });
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle(getString(R.string.elderly_monitored));
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
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
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadData();
        });
    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadData() {
        mAdapter.clearItems(); // clear list

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            Historical historical = new Historical.Query()
                    .type(HistoricalType.ELDERLIES_USER)
                    .params(params)
                    .build();

            historical.request(this, new CallbackHistorical<Elderly>() {
                @Override
                public void onBeforeSend() {
                    toggleLoading(true); // Enable loading
                    toggleNoDataMessage(false); // Disable message no data
                }

                @Override
                public void onError(JSONObject result) {
                    printError(result);
                }

                @Override
                public void onResult(List<Elderly> result) {
                    if (result != null && result.size() > 0) {
                        mAdapter.addItems(result);

                        saveLocal(result);
                    } else {
                        toggleNoDataMessage(true);
                    }
                }

                @Override
                public void onAfterSend() {
                    toggleLoading(false); // Disable loading
                }
            });
        }
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        mAdapter.addItems(elderlyDAO.list(session.getIdLogged()));

        if (mAdapter.itemsIsEmpty()) {
            toggleNoDataMessage(true); // Enable message no data
        }
        toggleLoading(false);
    }


    /**
     * Save Elderly in storage local.
     *
     * @param elderlies {@link List<Elderly>}
     */
    private void saveLocal(List<Elderly> elderlies) {
        if (elderlies == null) return;

        elderlyDAO.removeAll(session.getIdLogged());
        for (Elderly e : elderlies) elderlyDAO.save(e);
    }

    @Override
    protected void onStop() {
        super.onStop();
        server.cancelTagRequest(this.getClass().getName());
    }

    /**
     * Print message error.
     *
     * @param result
     */
    private void printError(JSONObject result) {
        try {
            if (result.has("message")) {
                if (!result.getString("message").equals("Canceled"))
                    printMessage(result.getString("message"));
            } else {
                printMessage(getString(R.string.error_500));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onItemClick(Elderly elderly) {
        if (itemClicked) return;

        Intent intentFall = new Intent(this, ElderlyFallActivity.class);
        intentFall.putExtra(ElderlyFallActivity.EXTRA_ELDERLY_ID, elderly.get_id());
        startActivity(intentFall);
        itemClicked = true;
    }

    @Override
    public void onLongItemClick(View v, Elderly item) {
        Log.w(TAG, "onLongItemClick()");
    }

    @Override
    public void onMenuContextClick(View v, Elderly elderly) {
        openDropMenu(v, elderly);
    }

    private void openDropMenu(View view, Elderly elderly) {
        Log.d(TAG, "openDropMenu() " + elderly);
        PopupMenu dropDownMenu = new PopupMenu(this, view);
        MenuInflater inflater = dropDownMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_drop_down_elderly_list, dropDownMenu.getMenu());

        if (elderly.getFallRisk() > 0) {
            dropDownMenu.getMenu().getItem(1).setTitle(R.string.action_new_fall_risk_assessment);
        }

        dropDownMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_elderly_view_infor:
                    Intent intent = new Intent(this, ElderlyPreviewActivity.class);
                    intent.putExtra(ElderlyPreviewActivity.EXTRA_ELDERLY_ID, elderly.get_id());
                    startActivity(intent);
                    break;
                case R.id.action_elderly_fall_risk:
                    Intent intentFallRisk = new Intent(this, FallRiskActivity.class);
                    intentFallRisk.putExtra(FallRiskActivity.EXTRA_ELDERLY_ID, elderly.get_id());
                    startActivity(intentFallRisk);
                    break;
                case R.id.action_elderly_delete_device_association:
                    openDialogConfirmRemoveAssociation(elderly);
                    break;
                case R.id.action_elderly_delete:
                    Toast.makeText(getApplicationContext(), "action_elderly_delete " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            return true;
        });
        dropDownMenu.show();
    }

    /**
     * Dialog confirmation to remove association with device.
     */
    private void openDialogConfirmRemoveAssociation(Elderly elderly) {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setTitle(R.string.warning_title)
                    .setMessage(getResources().getString(R.string.elderly_confirm_delete_device_association, elderly.getName()))
                    .setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                        // TODO Relizar rotina para remover o pin/atualizar view/atualizar elderly local e remoto.
                    })
                    .setNegativeButton(R.string.no_text, null)
                    .create().show();
        });
    }
}
