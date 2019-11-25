package br.edu.uepb.nutes.haniot.activity;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.PilotStudyAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.Synchronize;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.ErrorHandler;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import static br.edu.uepb.nutes.haniot.data.model.type.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.HEALTH_PROFESSIONAL;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.PATIENT;

/**
 * Implementation of the pilot study selection getAllByUserId.
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

    @BindView(R.id.content_error)
    LinearLayout errorPilotStudy;

    @BindView(R.id.content)
    FrameLayout content;

    @BindView(R.id.icon_error)
    ImageView iconError;

    @BindView(R.id.message_error_server)
    TextView messageError;

    @BindView(R.id.message_error_server_title)
    TextView titleError;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    private PilotStudyAdapter mPilotStudyAdapter;
    private AppPreferencesHelper appPreferences;
    private Repository mRepository;
    private CompositeDisposable mComposite;
    private User user; // Health Professional

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot_study);
        ButterKnife.bind(this);

        appPreferences = AppPreferencesHelper.getInstance(this);
        mRepository = Repository.getInstance(this);
        mComposite = new CompositeDisposable();

        user = appPreferences.getUserLogged();

        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mComposite.dispose();
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
     * @param enabled
     */
    private void showErrorPilot(boolean enabled) {
        if (enabled) {
            errorPilotStudy.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        } else {
            errorPilotStudy.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param enabled
     */
    private void showErrorConnection(boolean enabled) {
        if (enabled) {
            showErrorPilot(true);
            iconError.setImageResource(R.drawable.ic_error_server);
            titleError.setText("Opss! Houve algum erro.");
            messageError.setText(getText(R.string.error_500));
        } else {
            showErrorPilot(false);
            iconError.setImageResource(R.drawable.ic_no_pilot_study);
            titleError.setText("Opss! Você ainda não possui Piloto Estudo.");
            messageError.setText(getText(R.string.piloty_study_no_allocated));
        }
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

            @Override
            public void onItemSwiped(PilotStudy item, int position) {

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
        pilot.setSelected(true);
        pilot.setUserId(user.get_id());
        user.setPilotStudyIDSelected(pilot.get_id());
        appPreferences.removeLastPilotStudy();
        appPreferences.saveLastPilotStudy(pilot);
        appPreferences.saveUserLogged(user);
        Synchronize.getInstance(this).synchronize();

        if (user.getUserType().equals(PATIENT)) {
            Patient patient = new Patient();
            patient.set_id(user.get_id());
            patient.setPilotStudyIDSelected(pilot.get_id());
            mComposite.add(mRepository.updatePatient(patient).subscribe(patient1 -> {
                openDashboard();
            }, throwable -> {
                Log.w("AAA", throwable.getMessage());
                ErrorHandler.showMessage(this, throwable);
            }));
        } else if (user.getUserType().equals(ADMIN)) {
            Admin admin = new Admin();
            admin.set_id(user.get_id());
            admin.setPilotStudyIDSelected(pilot.get_id());
            mComposite.add(mRepository.updateAdmin(admin).subscribe(admin1 -> {
                openDashboard();
            }, throwable -> {
                Log.w("AAA", throwable.getMessage());
                ErrorHandler.showMessage(this, throwable);
            }));
        } else if (user.getUserType().equals(HEALTH_PROFESSIONAL)) {
            HealthProfessional healthProfessional = new HealthProfessional();
            healthProfessional.set_id(user.get_id());
            healthProfessional.setPilotStudyIDSelected(pilot.get_id());
            mComposite.add(mRepository.updateHealthProfissional(healthProfessional).subscribe(healthProfessional1 -> {
                openDashboard();
            }, throwable -> {
                Log.w("AAA", throwable.getMessage());
                ErrorHandler.showMessage(this, throwable);
            }));
        }
    }

    private void openDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
            populatePilotStudiesView(null);
            return;
        }

        if (appPreferences.getUserLogged().getUserType().equals(ADMIN)) {
            mComposite.add(mRepository
                    .getAllPilotStudies()
                    .doOnSubscribe(disposable -> showLoading(true))
                    .doAfterTerminate(() -> showLoading(false))
                    .subscribe(pilotStudies -> {
                        for (PilotStudy pilot : pilotStudies) {
                            if (user.getPilotStudyIDSelected() != null && pilot.get_id().equals(user.getPilotStudyIDSelected())) {
                                pilot.setSelected(true);
                            }
                        }
                        populatePilotStudiesView(pilotStudies);
                    }, error -> {
                        populatePilotStudiesView(null);
                    })
            );
        } else {
            mComposite.add(mRepository
                    .getAllUserPilotStudies(appPreferences.getUserLogged().get_id())
                    .doOnSubscribe(disposable -> showLoading(true))
                    .doAfterTerminate(() -> showLoading(false))
                    .subscribe(pilotStudies -> {
                        for (PilotStudy pilot : pilotStudies) {
                            if (user.getPilotStudyIDSelected() != null && pilot.get_id().equals(user.getPilotStudyIDSelected())) {
                                pilot.setSelected(true);
                            }
                        }
                        populatePilotStudiesView(pilotStudies);
                    }, error -> {
                        populatePilotStudiesView(null);
                    })
            );
        }
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void populatePilotStudiesView(List<PilotStudy> pilotStudies) {
        mPilotStudyAdapter.clearItems();
        mPilotStudyAdapter.addItems(pilotStudies);

        if (pilotStudies == null) {
            showErrorConnection(true);
        } else if (pilotStudies.isEmpty()) {
//            showNoDataMessage(true);
            showErrorConnection(false);
            showErrorPilot(true);
            showInstructionsMessage(false);
        } else {
//            showNoDataMessage(false);
            showErrorConnection(false);
            showErrorPilot(false);
            if (user.getPilotStudyIDSelected() != null) { // Pilot is selected
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
