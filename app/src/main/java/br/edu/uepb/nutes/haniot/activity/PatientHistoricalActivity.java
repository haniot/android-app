package br.edu.uepb.nutes.haniot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientHistoricalActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_historical);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.patient_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

    }

    private void initComponents() {
        Patient patient = getIntent().getParcelableExtra("Patient");
        if (patient != null && patient instanceof Patient){
            Log.d("TESTE","Paciente recebido nome: "+patient.getName());
            Log.d("TESTE","Paciente recebido id: "+patient.get_id());
            Log.d("TESTE","Paciente recebido idade: "+patient.getAge());
            Log.d("TESTE","Paciente recebido sexo: "+patient.getSex());
            Log.d("TESTE","Paciente recebido cor: "+patient.getColor());
            Log.d("TESTE","Paciente recebido data de registro: "+patient.getRegisterDate());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
