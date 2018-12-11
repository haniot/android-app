package br.edu.uepb.nutes.haniot.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.vicmikhailau.maskededittext.MaskedEditText;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientHistoricalActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textId)
    TextInputEditText textId;

    @BindView(R.id.textName)
    TextInputEditText textName;

    @BindView(R.id.textColor)
    TextInputEditText textColor;

    @BindView(R.id.textAge)
    TextInputEditText textAge;

    @BindView(R.id.textRegisterDate)
    MaskedEditText textRegisterDate;

    @BindView(R.id.textIdProfessionalResponsible)
    TextInputEditText textIdProfessionalResponsible;

    @BindView(R.id.radioPatientMale)
    AppCompatRadioButton radioMale;

    @BindView(R.id.radioPatientFemale)
    AppCompatRadioButton radioFemale;

    @BindView(R.id.btnSurvey)
    AppCompatButton btnSurvey;

    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;

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
        textId.setText(patient.get_id());
//        textId.setFocusable(false);
        textName.setText(patient.getName());
        textName.setFocusable(false);
        textColor.setText(patient.getColor());
        textColor.setFocusable(false);
        textAge.setText(String.valueOf(patient.getAge()));
        textAge.setFocusable(false);
        textRegisterDate.setText(patient.getRegisterDate());
        textRegisterDate.setFocusable(false);
        textIdProfessionalResponsible.setText(String.valueOf(patient.getIdProfessionalResponsible()));
        textIdProfessionalResponsible.setFocusable(false);
        if (patient.getSex().equals("male")){
            radioMale.setChecked(true);
            radioFemale.setChecked(false);
        }else{
            radioFemale.setChecked(true);
            radioMale.setChecked(false);
        }
        btnConfirm.setEnabled(false);
        btnSurvey.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        hideKeyboard(this);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_patient_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void enableButtons(){
        textId.setEnabled(true);
//        textId.setFocusable(true);
        textName.setEnabled(true);
        textName.setFocusable(true);
        textColor.setEnabled(true);
        textColor.setFocusable(true);
        textAge.setEnabled(true);
        textAge.setFocusable(true);
        textRegisterDate.setEnabled(true);
        textRegisterDate.setFocusable(true);
        textIdProfessionalResponsible.setEnabled(true);
        textIdProfessionalResponsible.setFocusable(true);
        btnConfirm.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.btnEdit:
                enableButtons();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btnSurvey:
                Log.d("TESTE","Abrir activity do questionario");
                break;
            case R.id.btnConfirm:
                Log.d("TESTE","Salvar atualizações do paciente");
                break;
        }

    }
}
