package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.ManagerPatientAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

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
    private SearchView searchView;
    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;
    private PilotStudy pilotStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patient);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.manage_patient));

        //TODO TEMP
        toolbar.setOnClickListener(v -> {
            startActivity(new Intent(this, PatientQuizOdontoActivity.class));
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initResources();
        loadData();
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
        message.setVisibility(View.INVISIBLE);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        pilotStudy = appPreferencesHelper.getLastPilotStudy();
        patientList = new ArrayList<>();
        disableBack();

        addPatient.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PatientRegisterActivity.class));
            finish();
        });
        addPatientShortCut.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PatientRegisterActivity.class));
            finish();
        });
        initDataSwipeRefresh();
    }

    private void loadData() {
        DisposableManager.add(haniotNetRepository
                .getAllPatients(pilotStudy.get_id(), "created_at", 1, 100)
                .doOnSubscribe(disposable -> mDataSwipeRefresh.setRefreshing(true))
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
        else Toast.makeText(this,
                getResources().getString(R.string.no_patient_registered),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);
            Log.i(LOG_TAG, httpEx.getMessage());
            showMessage(getResources().getString(R.string.error_500));
        }
        // message 500
    }

    private void initRecyclerView() {
        adapter = new ManagerPatientAdapter(this);
        adapter.setListener(new OnRecyclerViewListener<Patient>() {
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
                if (v.getId() == R.id.btnDeleteChild) {
                    new AlertDialog
                            .Builder(ManagerPatientsActivity.this)
                            .setMessage(getResources().getString(R.string.remove_patient))
                            .setPositiveButton(getResources().getText(R.string.yes_text), (dialog, which) -> removePatient(item))
                            .setNegativeButton(getResources().getText(R.string.no_text), null)
                            .show();

                } else if (v.getId() == R.id.btnEditChildren) {
                    Intent intent = new Intent(ManagerPatientsActivity.this, PatientRegisterActivity.class);
                    intent.putExtra("action", "edit");
                    appPreferencesHelper.saveLastPatient(item);
                    startActivity(intent);
                    finish();
                }
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
        DisposableManager.add(haniotNetRepository
                .deletePatient(pilotStudy.get_id(), patient.get_id())
                .doAfterTerminate(this::loadData)
                .subscribe(() -> {
                            adapter.removeItem(patient);
                            adapter.notifyDataSetChanged();
                            showMessage(getResources().getString(R.string.patient_removed));
                            if (patient.get_id().equals(appPreferencesHelper.getLastPatient().get_id()))
                                appPreferencesHelper.removeLastPatient();
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
    public boolean onCreateOptionsMenu(Menu menu) {

        //Adiciona o menu a activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_patient, menu);

        //Botão search na toolbar
        MenuItem searchBtn = menu.findItem(R.id.btnSearchPatient);
        this.searchView = (SearchView) searchBtn.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);

        return super.onCreateOptionsMenu(menu);
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
