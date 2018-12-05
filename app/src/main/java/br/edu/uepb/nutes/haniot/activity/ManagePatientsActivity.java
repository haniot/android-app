package br.edu.uepb.nutes.haniot.activity;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.ManagePatientAdapter;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagePatientsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewPatient)
    RecyclerView recyclerViewPatient;

    private List<Patient> patientList = new ArrayList<>();
    private ManagePatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patient);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.manage_children));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
        initComponents();

    }

    private void initComponents(){

        List<Patient> patients;

        patients = PatientDAO.getInstance(getApplicationContext()).get();

        if (patients.size() <= 0) {
            adapter = new ManagePatientAdapter(this.patientList, getApplicationContext());
        }else{
            adapter = new ManagePatientAdapter(patients, getApplicationContext());
        }
        recyclerViewPatient.setHasFixedSize(true);
        recyclerViewPatient.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPatient.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                Patient patient = adapter.getPatient(position);

                if (patient != null) {
                    Log.d("TESTE", "Activity, enviando paciente para ser removido: " + patient.getName());
                    adapter.removeItem(patient,adapter.searchPositionByChildrenId(patient.get_id()));
                }else{
                    Log.d("TESTE", "Paciente null");
                }

                Snackbar snackbar = Snackbar
                        .make(recyclerViewPatient, getResources().getString(R.string.patient_removed)
                                , Snackbar.LENGTH_LONG).setAction(getResources()
                                .getString(R.string.undo), view -> {
                            adapter.restoreItem(patient,position);
                            recyclerViewPatient.scrollToPosition(position);
                        });
                snackbar.show();
            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.START |
                        ItemTouchHelper.END);
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
        List<String> l = new ArrayList<String>();
        l.add("joao");
        l.add("tiago");
        l.add("jonatas");
        l.add("felipe");
        l.add("maria");
        l.add("jose");
        l.add("lucas");
        l.add("ramom");
        l.add("luis");
        l.add("josue");

        Patient patient;
        SimpleDateFormat spn = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < 10; i++) {
            patient = new Patient();
            patient.set_id(String.valueOf(i * 65478));
            patient.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
            patient.setName(l.get(i));
            patientList.add(patient);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Adiciona o menu a activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_patient, menu);

        //Botão search na toolbar
        MenuItem searchBtn = menu.findItem(R.id.btnSearchPatient);
        final SearchView searchView = (SearchView) searchBtn.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.LTGRAY);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();

                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setSearchQuerry(newText);
                adapter.getFilter().filter(newText);
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

                for (Patient patient : patientList) {
                    if (PatientDAO.getInstance(getApplicationContext()).save(patient)){

                    }
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
