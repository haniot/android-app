package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
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
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

/**
 * ManagePatientsActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class ManagePatientsActivity extends AppCompatActivity implements OnRecyclerViewListener<Patient> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewPatient)
    RecyclerView recyclerViewPatient;
    @BindView(R.id.btnAddPatient)
    FloatingActionButton addPatient;
    @BindView(R.id.manager_patients_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;
    private List<Patient> patientList;
    private ManagerPatientAdapter adapter;
    private SearchView searchView;
    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;
    private PilotStudy pilotStudy;

    private ImageView iconEmpty;
    private TextView textEmpty1;
    private TextView textEmpty2;
    private TextView textButton;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patient);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.manage_patient));
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
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        pilotStudy = appPreferencesHelper.getLastPilotStudy();
        patientList = new ArrayList<>();
        if (appPreferencesHelper.getLastPatient() == null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        addPatient.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PatientRegisterActivity.class));
            finish();
        });
        initDataSwipeRefresh();
    }

    /**
     * On click item of list patients.
     *
     * @param item
     */
    @Override
    public void onItemClick(Patient item) {
        AppPreferencesHelper.getInstance(this).saveLastPatient(item);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onLongItemClick(View v, Patient patient) {

    }

    @Override
    public void onMenuContextClick(View v, Patient patient) {
        switch (v.getId()) {
            case R.id.btnDeleteChild:
                //TODO Refatorar Colocar dialog de confirmação, dar update no listview
                haniotNetRepository.deletePatient(pilotStudy.get_id(), patient.get_id())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(ManagePatientsActivity.this,
                                        "Paciente removido!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                break;
            case R.id.btnEditChildren:
                appPreferencesHelper.saveLastPatient(patient);
                //TODO Passar flag para editar
                startActivity(new Intent(this, PatientRegisterActivity.class));
                break;
        }
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
            Toast.makeText(this, getResources().getString(R.string.error_500), Toast.LENGTH_LONG).show();
        }
        // message 500
    }

    private void initRecyclerView() {
        //  empty(true);
        // empty(false);
        adapter = new ManagerPatientAdapter(this);
        adapter.setListener(new OnRecyclerViewListener<Patient>() {
            @Override
            public void onItemClick(Patient item) {
                appPreferencesHelper.saveLastPatient(item);
                startActivity(new Intent(ManagePatientsActivity.this, MainActivity.class));
            }

            @Override
            public void onLongItemClick(View v, Patient item) {

            }

            @Override
            public void onMenuContextClick(View v, Patient item) {

            }
        });

        recyclerViewPatient.setHasFixedSize(true);
        recyclerViewPatient.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPatient.setAdapter(adapter);

        adapter.addItems(patientList);
    }

    /**
     * For tests.
     *
     * @return
     */
    private List<Patient> testPatients() {
        patientList.clear();
        Patient patient;

        patient = appPreferencesHelper.getLastPatient();
        if (patient != null)
            patientList.add(patient);

        patient = new Patient();
        patient.setFirstName("Fábio Júnior");
        patient.set_id("123");
        patient.setGender("Male");
        patient.setBirthDate(DateUtils.formatDate(878180400000L, DateUtils.DATE_FORMAT_ISO_8601));
        patientList.add(patient);

        patient = new Patient();
        patient.setFirstName("Paulo Barbosa");
        patient.set_id("124");
        patient.setGender("male");
        patient.setBirthDate(DateUtils.formatDate(878180400000L, DateUtils.DATE_FORMAT_ISO_8601));

        patientList.add(patient);

        patient = new Patient();
        patient.setFirstName("Ana Beatriz");
        patient.set_id("125");
        patient.setGender("female");
        patient.setBirthDate(DateUtils.formatDate(878180400000L, DateUtils.DATE_FORMAT_ISO_8601));
        patientList.add(patient);

        patient = new Patient();
        patient.setFirstName("Isabele");
        patient.set_id("126");
        patient.setGender("female");
        patient.setBirthDate(DateUtils.formatDate(878180400000L, DateUtils.DATE_FORMAT_ISO_8601));
        patientList.add(patient);

        patient = new Patient();
        patient.setFirstName("Paulina Leal");
        patient.set_id("127");
        patient.setGender("female");
        patient.setBirthDate(DateUtils.formatDate(878180400000L, DateUtils.DATE_FORMAT_ISO_8601));
        patientList.add(patient);

        patient = new Patient();
        patient.setFirstName("Douglas Rafael");
        patient.set_id("128");
        patient.setGender("male");
        patient.setBirthDate(DateUtils.formatDate(878180400000L, DateUtils.DATE_FORMAT_ISO_8601));

        patientList.add(patient);

        return patientList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
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
