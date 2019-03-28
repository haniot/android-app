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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.ManagePatientAdapter;
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
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
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
    private ManagePatientAdapter adapter;
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
                haniotNetRepository.deletePatient("5c86d00c2239a48ea20a0134", patient.get_id())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(ManagePatientsActivity.this,
                                        "Paciente removido!", Toast.LENGTH_SHORT);
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
        adapter = new ManagePatientAdapter(patientList, getApplicationContext(), this);

        recyclerViewPatient.setHasFixedSize(true);
        recyclerViewPatient.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPatient.setAdapter(adapter);

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                getApplicationContext(), resId);
        recyclerViewPatient.setLayoutAnimation(animation);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                Patient patient = adapter.getPatient(position);

                Snackbar snackbar = null;
                if (patient != null) {
                    if (adapter.getSearchQuerry().isEmpty()) {
                        int newPosition = adapter.searchOldPatientPosition(patient);

                        adapter.removeItem(patient, newPosition
                                , adapter.REMOVE_TYPE_NOT_FILTERED);
                        snackbar = Snackbar
                                .make(recyclerViewPatient, getResources().getString(R.string.patient_removed)
                                        , Snackbar.LENGTH_LONG).setAction(getResources()
                                        .getString(R.string.undo), view -> {
                                    adapter.restoreItem(patient, newPosition, viewHolder.itemView);
                                });
                    } else {
                        adapter.removeItem(patient, viewHolder.getAdapterPosition()
                                , adapter.REMOVE_TYPE_FILTERED);
                        snackbar = Snackbar
                                .make(recyclerViewPatient, getResources().getString(R.string.patient_removed)
                                        , Snackbar.LENGTH_LONG).setAction(getResources()
                                        .getString(R.string.undo), view -> {
                                    adapter.restoreItem(patient, position, viewHolder.itemView);
                                });
                    }
                }
                if (snackbar != null) snackbar.show();
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (adapter.getItemsSize() > 0) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START |
                            ItemTouchHelper.END);
                }
                return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, 0);
            }

            @Override
            public void onChildDraw(Canvas c,
                                    RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX,
                                    float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth());
                    viewHolder.itemView.setAlpha(alpha);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewPatient);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
//                adapter.getFilter().filter(query);
                adapter.setSearchQuerry(query);
                adapter.filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setSearchQuerry(newText);
//                adapter.getFilter().filter(newText);
                adapter.filter(newText);
                return false;
            }

        });

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
}
