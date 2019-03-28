package br.edu.uepb.nutes.haniot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.PilotStudyAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.dao.PilotStudyDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Implementation of the pilot study selection list.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class PilotStudyActivity extends AppCompatActivity {
    private final String LOG_TAG = "PilotStudyActivity";
    public static final int REQUEST_PILOT_STUDY_SELECTED = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pilots_studies_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.pilots_studies_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.box_instruction_message)
    LinearLayout boxInstructionMessage;

    @BindView(R.id.box_no_data_message)
    LinearLayout boxNoDataMessage;

    @BindView(R.id.info_inactive_selected)
    TextView infoInactiveSelectedMessage;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    private PilotStudyAdapter mPilotStudyAdapter;
    private PilotStudyDAO pilotStudyDAO;
    private AppPreferencesHelper appPreferences;
    private HaniotNetRepository haniotNetRepository;
    private User user; // Health Professional

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot_study);
        ButterKnife.bind(this);

        pilotStudyDAO = PilotStudyDAO.getInstance(this);
        appPreferences = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);

        user = appPreferences.getUserLogged();

        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initToolBar();
        initRecyclerView();
        initDataSwipeRefresh();
        loadData();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.piloty_study_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Init RecyclerView
     */
    private void initRecyclerView() {
        mPilotStudyAdapter = new PilotStudyAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mPilotStudyAdapter.setListener(new OnRecyclerViewListener<PilotStudy>() {
            @Override
            public void onItemClick(PilotStudy pilot) {
                if (pilot.isSelected()) {
                    dialogPilotIsSelected(pilot);
                    return;
                }
                dialogConfirmPilotSelected(pilot);
            }

            @Override
            public void onLongItemClick(View v, PilotStudy pilot) {

            }

            @Override
            public void onMenuContextClick(View v, PilotStudy pilot) {

            }
        });

        mRecyclerView.setAdapter(mPilotStudyAdapter);
    }

    /**
     * Opens the dialog to confirm the pilot selection.
     *
     * @param pilot {@link PilotStudy}
     */
    private void dialogConfirmPilotSelected(PilotStudy pilot) {
        new AlertDialog
                .Builder(this)
                .setTitle(R.string.attention)
                .setIcon(R.drawable.ic_action_warning)
                .setMessage(getString(R.string.piloty_study_confirm_selected, pilot.getName()))
                .setPositiveButton(R.string.yes_text, (dialog, which) -> savePilotSelected(pilot))
                .setNegativeButton(R.string.no_text, null)
                .show();

    }

    /**
     * Opens the dialog to inform you that the
     * pilot has already been selected.
     *
     * @param pilot {@link PilotStudy}
     */
    private void dialogPilotIsSelected(PilotStudy pilot) {
        new AlertDialog
                .Builder(this)
                .setTitle(R.string.attention)
                .setIcon(R.drawable.ic_action_warning)
                .setMessage(getString(R.string.piloty_study_is_selected, pilot.getName()))
                .setPositiveButton(R.string.text_ok, null)
                .show();
    }

    /**
     * Save pilot study selected.
     *
     * @param pilot {@link PilotStudy}
     */
    private void savePilotSelected(PilotStudy pilot) {
        pilotStudyDAO.clearSelected(user.get_id());

        pilot.setSelected(true);
        pilotStudyDAO.update(pilot);
        appPreferences.saveLastPilotStudy(pilot);
        appPreferences.removeLastPatient();
        // Back activity
        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadData();
        });
    }

    /**
     * Load pilot studies remote or local.
     */
    private void loadData() {
        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
            return;
        }

        DisposableManager.add(haniotNetRepository
                .getAllPilotStudies(appPreferences.getUserLogged().get_id())
                .doOnSubscribe(disposable -> showLoading(true))
                .doAfterTerminate(() -> showLoading(false))
                .subscribe(pilotStudies -> {
                    // Updates locally saved pilots
                    if (pilotStudies.isEmpty()) pilotStudyDAO.removeAll(user.get_id());
                    for (PilotStudy pilot : pilotStudies) {
                        PilotStudy pilotCurrent = pilotStudyDAO.get(pilot.get_id());
                        if (pilotCurrent != null) {
                            pilot.setId(pilotCurrent.getId());
                            pilot.setUserId(user.get_id());
                            pilot.setSelected(pilotCurrent.isSelected());
                            pilotStudyDAO.update(pilot); // update or create
                        } else {
                            pilot.setUserId(user.get_id());
                            pilotStudyDAO.save(pilot);
                        }
                    }
                    loadDataLocal();
                }, error -> {
                    Log.w(LOG_TAG, "loadData() error: " + error.getMessage());
                    loadDataLocal();
                })
        );
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        mPilotStudyAdapter.clearItems();
        mPilotStudyAdapter.addItems(pilotStudyDAO.list(user.get_id()));

        if (mPilotStudyAdapter.itemsIsEmpty()) {
            showNoDataMessage(true);
            showInstructionsMessage(false);
        } else {
            showNoDataMessage(false);
            if (appPreferences.getLastPilotStudy() != null) { // Pilot is selected
                showInstructionsMessage(false);
            } else {
                showInstructionsMessage(true);
            }
        }
        showLoading(false);
    }

    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     */
    private void showLoading(boolean enabled) {
        runOnUiThread(() -> {
            if (!enabled) {
                mDataSwipeRefresh.setRefreshing(false);
                itShouldLoadMore = true;
            } else {
                mDataSwipeRefresh.setRefreshing(true);
                itShouldLoadMore = false;
            }
        });
    }

    /**
     * Enable/Disable display no data message.
     *
     * @param display boolean
     */
    private void showNoDataMessage(boolean display) {
        runOnUiThread(() -> {

            if (!display) {
                boxNoDataMessage.setVisibility(View.GONE);
                return;
            }
            boxNoDataMessage.startAnimation(AnimationUtils
                    .loadAnimation(this, android.R.anim.fade_in));
            boxNoDataMessage.setVisibility(View.VISIBLE);
            infoInactiveSelectedMessage.setVisibility(View.GONE);
        });
    }

    /**
     * Enable/Disable display instructions message.
     *
     * @param display boolean
     */
    private void showInstructionsMessage(boolean display) {
        runOnUiThread(() -> {
            if (!display) {
                boxInstructionMessage.setVisibility(View.GONE);
                return;
            }
            boxInstructionMessage.startAnimation(AnimationUtils
                    .loadAnimation(this, android.R.anim.fade_in));
            boxInstructionMessage.setVisibility(View.VISIBLE);
            infoInactiveSelectedMessage.setVisibility(View.VISIBLE);
        });
    }
}
