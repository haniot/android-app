package br.edu.uepb.nutes.haniot.elderly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

/**
 * ElderlyMonitoredActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
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

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.no_data_textView)
    TextView mNoDataTextView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.elderlies_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.elderly_add_floating_button)
    FloatingActionButton mAddPatientButton;


    private ActionMode mActionMode;
    private ActionBar mActionBar;

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
        mActionBar = getSupportActionBar();
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
//        startActivity(new Intent(getApplicationContext(), FallRiskActivity.class));
    }


    @Override
    public void onLongItemClick(View v, Elderly item) {
        Log.w(TAG, "onLongItemClick()");

//        mRecyclerView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
//            contextMenu.add(Menu.NONE, 1, Menu.NONE, "editar");
//            contextMenu.add(Menu.NONE, 2, Menu.NONE, "deletar");
//        });
//        mActionMode = this.startSupportActionMode(mActionModeCallback);
        mToolbar.startActionMode(mActionModeCallback);
        v.setSelected(true);
    }

    @Override
    public void onMenuContextClick(View v, Elderly item) {
        openDropMenu(v, item);
    }

    private void openDropMenu(View view, Elderly item) {
        PopupMenu dropDownMenu = new PopupMenu(this, view);
        MenuInflater inflater = dropDownMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_drop_down_elderly_list, dropDownMenu.getMenu());
        dropDownMenu.show();
        dropDownMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_fall_risk:
                    Toast.makeText(getApplicationContext(), "action_fall_risk " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                    break;
                case R.id.action_fall_list:
                    Toast.makeText(getApplicationContext(), "action_fall_list " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                    break;
                case R.id.action_elderly_delete:
                    Toast.makeText(getApplicationContext(), "action_elderly_delete " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        });
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_drop_down_elderly_list, menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_share:
//                    shareCurrentItem();
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
//                default:
//                    return false;
//            }
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

}
