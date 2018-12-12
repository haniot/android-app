package br.edu.uepb.nutes.haniot.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientHistoricalActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textId)
    TextInputEditText textId;

    @BindView(R.id.layoutName)
    TextInputLayout layoutName;

    @BindView(R.id.textName)
    TextInputEditText textName;

    @BindView(R.id.layoutColor)
    TextInputLayout layoutColor;

    @BindView(R.id.textColor)
    TextInputEditText textColor;

    @BindView(R.id.layoutAge)
    TextInputLayout layoutAge;

    @BindView(R.id.textAge)
    TextInputEditText textAge;

    @BindView(R.id.layoutDate)
    TextInputLayout layoutDate;

    @BindView(R.id.textRegisterDate)
    TextInputEditText textRegisterDate;

    @BindView(R.id.layoutIdProfessionalResponsible)
    TextInputLayout layoutIdProfessional;

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

    private Patient patient;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

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
        this.patient = getIntent().getParcelableExtra("Patient");
        textId.setText(patient.get_id());
        textName.setText(patient.getName());
        textColor.setText(patient.getColor());
        textAge.setText(String.valueOf(patient.getAge()));
        textRegisterDate.setText(patient.getRegisterDate());
        textRegisterDate.setOnClickListener(this);
        textRegisterDate.setInputType(InputType.TYPE_NULL);
        textRegisterDate.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                openDatePicker();
            }
        });
        layoutDate.setOnClickListener(this);
        textIdProfessionalResponsible.setText(String.valueOf(patient.getIdProfessionalResponsible()));
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

        calendar = Calendar.getInstance();

//        Close keyboard when activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void openDatePicker() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(PatientHistoricalActivity.this
                , this, year, month, day);

        //Essa parte precisa ser assim pois em algumas versões do android ao setar o max date ele
        //buga ao tentar selecionar o ultimo dia
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.HOUR_OF_DAY, 23);
        maxDate.set(Calendar.MINUTE, 59);
        maxDate.set(Calendar.SECOND, 59);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();

        /**
         * Close keyboard
         */
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(textRegisterDate.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_patient_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private boolean validate(){
        boolean status = true;

        String id = textId.getText().toString();
        if (id.isEmpty()){
            status = false;
            textId.setError(getResources().getString(R.string.required_field));
        }

        String name = textName.getText().toString();
        if (name.isEmpty()){
            status = false;
            layoutName.setErrorEnabled(true);
            layoutName.setError(getResources().getString(R.string.required_field));

        }else{
            layoutName.setErrorEnabled(false);
            this.patient.setName(name);
        }

        String gender = "female";
        if (radioMale.isChecked()){
            gender = "male";
        }
        this.patient.setSex(gender);

        String color = textColor.getText().toString();
        if (color.isEmpty()){
            status = false;
            layoutColor.setErrorEnabled(true);
            layoutColor.setError(getResources().getString(R.string.required_field));
        }else{
            layoutColor.setErrorEnabled(false);
            this.patient.setColor(color);
        }

        String age = textAge.getText().toString();
        if (age.isEmpty()){
            status = false;
            layoutAge.setErrorEnabled(true);
            layoutAge.setError(getResources().getString(R.string.required_field));
        }else{
            layoutAge.setErrorEnabled(false);
            this.patient.setAge(Integer.parseInt(age));
        }

        String date = textRegisterDate.getText().toString();
        if (date.isEmpty()){
            status = false;
            layoutDate.setErrorEnabled(true);
            layoutDate.setError(getResources().getString(R.string.required_field));
        }else{
            layoutDate.setErrorEnabled(false);
            this.patient.setRegisterDate(date);
        }

        String idProfessional = textIdProfessionalResponsible.getText().toString();
        if (idProfessional.isEmpty()){
            status = false;
            layoutIdProfessional.setErrorEnabled(true);
            layoutIdProfessional.setError(getResources().getString(R.string.required_field));
        }else{
            layoutIdProfessional.setErrorEnabled(false);
            this.patient.setIdProfessionalResponsible(Integer.parseInt(idProfessional));
        }

        return status;
    }

    private void enableButtons(){
//        textId.setEnabled(true);
        textName.setEnabled(true);
        textColor.setEnabled(true);
        textAge.setEnabled(true);
        textRegisterDate.setEnabled(true);
        textIdProfessionalResponsible.setEnabled(true);
        radioMale.setEnabled(true);
        radioFemale.setEnabled(true);
        btnConfirm.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
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
                if (validate()){
                    if (PatientDAO.getInstance(getApplicationContext()).update(this.patient)){
                        Log.d("TESTE","Atualizado com sucesso; paciente: "+this.patient.getName());
                        finish();
                    }else{
                        Log.d("TESTE","Erro ao atualizar");
                    }
                }else{
                    Log.d("TESTE","Erro ao validar");
                }
                break;
            case R.id.textRegisterDate:
                hideSoftKeyboard(this);
                openDatePicker();
                break;
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        this.calendar.set(year,month,dayOfMonth);

        String date = DateUtils
                .calendarToString(this.calendar,getResources().getString(R.string.date_format));
        updateTextDate(date);

        textIdProfessionalResponsible.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    private void updateTextDate(String date){
        this.textRegisterDate.setText(date);
    }

}
