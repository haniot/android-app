package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.ManagePatientAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagePatientsActivity extends AppCompatActivity implements OnRecyclerViewListener<Patient> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewPatient)
    RecyclerView recyclerViewPatient;

    private List<Patient> patientList = new ArrayList<>();
    private ManagePatientAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patient);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.manage_patient));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
        initComponents();

        Session session =  new Session(this);
        Log.i("AAAA", session.getUserLogged().toJson());
        AppPreferencesHelper.getInstance(this).saveUserLogged(session.getUserLogged());
        Log.i("AAA", AppPreferencesHelper.getInstance(this).getUserLogged().toString());

    }

    private void initComponents() {

        List<Patient> patients;

        patients = PatientDAO.getInstance(getApplicationContext()).get();

        adapter = new ManagePatientAdapter(patients, getApplicationContext(), this);

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
                    Log.d("TESTE", "Removendo o paciente " + patient.getFirstName());
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
                } else {
                    Log.d("TESTE", "Não encontrei o paciente");
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

    private void loadData() {

//        Patient patient;
//        SimpleDateFormat spn = new SimpleDateFormat("dd/MM/yyyy");
//
//        patient = new Patient();
//        patient.setFirstName("Fábio Júnior");
//        patient.set_id("123");
//        patient.setGender("Masculino");
//        patient.setBirthDate(DateUtils.formatDate(38323223, DateUtils.DATE_FORMAT_ISO_8601));
//        //patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
//        patientList.add(patient);
//
//        patient = new Patient();
//        patient.setFirstName("Paulo Barbosa");
//        patient.set_id("124");
//        patient.setGender("Masculino");
//        patient.setBirthDate(DateUtils.formatDate(38323223, DateUtils.DATE_FORMAT_ISO_8601));
//        //patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
//        patientList.add(patient);
//
//        patient = new Patient();
//        patient.setFirstName("Ana Beatriz");
//        patient.set_id("125");
//        patient.setGender("Feminino");
//        patient.setBirthDate(DateUtils.formatDate(38323223, DateUtils.DATE_FORMAT_ISO_8601));
//        //patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
//        patientList.add(patient);
//
//        patient = new Patient();
//        patient.setFirstName("Isabele");
//        patient.set_id("126");
//        patient.setGender("Feminino");
//        patient.setBirthDate(DateUtils.formatDate(38323223, DateUtils.DATE_FORMAT_ISO_8601));
//        //patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
//        patientList.add(patient);
//
//        patient = new Patient();
//        patient.setFirstName("Paulina Leal");
//        patient.set_id("127");
//        patient.setGender("Feminino");
//        patient.setBirthDate(DateUtils.formatDate(38323223, DateUtils.DATE_FORMAT_ISO_8601));
//        //patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
//        patientList.add(patient);
//
//        patient = new Patient();
//        patient.setFirstName("Douglas Rafael");
//        patient.set_id("128");
//        patient.setGender("Masculino");
//        patient.setBirthDate(DateUtils.formatDate(38323223, DateUtils.DATE_FORMAT_ISO_8601));
//        //patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
//        patientList.add(patient);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initComponents();
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

            case R.id.btnAddPatient:

//                for (Patient patient : patientList) {
//                    if (PatientDAO.getInstance(getApplicationContext()).save(patient)) {
//
//                    }
//                }

                startActivity(new Intent(this, PatientRegisterActivity.class));
                break;
            case R.id.add_patient:
//                for (Patient patient : patientList) {
//                    if (PatientDAO.getInstance(getApplicationContext()).save(patient)) {
//
//                    }
//                }
                startActivity(new Intent(this, PatientRegisterActivity.class));

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Patient item) {
        AppPreferencesHelper.getInstance(this).saveLastPatient(item);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onLongItemClick(View v, Patient item) {

    }

    @Override
    public void onMenuContextClick(View v, Patient item) {

    }
}
