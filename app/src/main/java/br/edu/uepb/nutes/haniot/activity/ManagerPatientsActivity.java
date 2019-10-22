package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.adapter.ManagerPatientAdapter;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.ErrorHandler;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ManagerPatientsActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class ManagerPatientsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewPatient)
    RecyclerView recyclerViewPatient;
    @BindView(R.id.btnAddPatient)
    FloatingActionButton addPatient;
    @BindView(R.id.manager_patients_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;
    @BindView(R.id.root)
    RelativeLayout message;
    @BindView(R.id.add_patient)
    TextView addPatientShortCut;

    private List<Patient> patientList;
    private ManagerPatientAdapter adapter;
    private Repository mRepository;
    private SearchView searchView;
    private AppPreferencesHelper appPreferencesHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patient);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.manage_patient));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initResources();
    }


    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(this::loadData);
    }

    /**
     * Init resources.
     */
    private void initResources() {
        recyclerViewPatient.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && addPatient.isShown()) {
                    addPatient.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    addPatient.hide();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    addPatient.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        message.setVisibility(View.INVISIBLE);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        mRepository = Repository.getInstance(this);
        patientList = new ArrayList<>();
        user = appPreferencesHelper.getUserLogged();
        disableBack();

        View.OnClickListener onClickListener = v -> {
            startActivity(new Intent(this, UserRegisterActivity.class));
        };
        addPatient.setOnClickListener(onClickListener);
        addPatientShortCut.setOnClickListener(onClickListener);
        initDataSwipeRefresh();
    }

    /**
     * Load patients in server.
     */
    private void loadData() {
        if (!addPatient.isShown()) addPatient.show();
        mDataSwipeRefresh.setRefreshing(true);
        DisposableManager.add(mRepository
                .getAllPatients(user.getPilotStudyIDSelected(), "created_at", 1, 100)
                .doAfterTerminate(() -> mDataSwipeRefresh.setRefreshing(false))
                .subscribe(patients -> {
                    patientList = patients;

                    initRecyclerView();
                }, this::errorHandler));
        disableBack();
    }

    @Override
    public void onBackPressed() {
        if (appPreferencesHelper.getLastPatient() != null) super.onBackPressed();
        else startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        ErrorHandler.showMessage(this, e);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initRecyclerView() {
        adapter = new ManagerPatientAdapter(this);
        adapter.setPatientActionListener(new ManagerPatientAdapter.ActionsPatientListener() {
            @Override
            public void onMenuClick(String action, Patient patient) {
                if ("quiz_dentistry".equals(action)) {
                    appPreferencesHelper.saveLastPatient(patient);
                    startActivity(new Intent(ManagerPatientsActivity.this, QuizOdontologyActivity.class));
                } else if ("quiz_nutrition".equals(action)) {
                    appPreferencesHelper.saveLastPatient(patient);
                    startActivity(new Intent(ManagerPatientsActivity.this, QuizNutritionActivity.class));
                } else if ("nutrition_evaluation".equals(action)) {
                    appPreferencesHelper.saveLastPatient(patient);
                    startActivity(new Intent(ManagerPatientsActivity.this, NutritionalEvaluationActivity.class));
                } else if ("historic_quiz".equals(action)) {
                    appPreferencesHelper.saveLastPatient(patient);
                    startActivity(new Intent(ManagerPatientsActivity.this, HistoricQuizActivity.class));
                }
            }

            @Override
            public void onItemClick(Patient item) {
                appPreferencesHelper.saveLastPatient(item);
                startActivity(new Intent(ManagerPatientsActivity.this, MainActivity.class));
            }

            @Override
            public void onLongItemClick(View v, Patient item) {

            }

            @Override
            public void onMenuContextClick(View v, Patient item) {
                if (v.getId() == R.id.btnMore) {
                    new AlertDialog
                            .Builder(ManagerPatientsActivity.this)
                            .setMessage(getResources().getString(R.string.remove_patient))
                            .setPositiveButton(getResources().getText(R.string.yes_text), (dialog, which) -> removePatient(item))
                            .setNegativeButton(getResources().getText(R.string.no_text), null)
                            .show();

                } else if (v.getId() == R.id.btnEditChildren) {
                    Intent intent = new Intent(ManagerPatientsActivity.this, UserRegisterActivity.class);
                    intent.putExtra("action", "edit");
                    appPreferencesHelper.saveLastPatient(item);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemSwiped(Patient item, int position) {

            }
        });

        recyclerViewPatient.setHasFixedSize(true);
        recyclerViewPatient.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPatient.setAdapter(adapter);

        adapter.addItems(patientList);
        if (patientList.isEmpty()) message.setVisibility(View.VISIBLE);
        else message.setVisibility(View.INVISIBLE);
    }

    private void removePatient(Patient patient) {

        DisposableManager.add(mRepository
                .deletePatient(patient.get_id())
                .doAfterTerminate(this::loadData)
                .subscribe(() -> {
                            adapter.removeItem(patient);
                            adapter.notifyDataSetChanged();
                            showMessage(getResources().getString(R.string.patient_removed));
                            Patient lastPatient = appPreferencesHelper.getLastPatient();
                            if (lastPatient != null && patient.get_id().equals(lastPatient.get_id())) {
                                appPreferencesHelper.removeLastPatient();
                            }
                        },
                        error -> showMessage(getResources().getString(R.string.error_500))));
    }

    private void disableBack() {
        if (appPreferencesHelper.getLastPatient() == null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void showMessage(String message) {
        Toast.makeText(ManagerPatientsActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
    }
}
