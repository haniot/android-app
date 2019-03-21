package br.edu.uepb.nutes.haniot.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientRegisterActivity extends AppCompatActivity {

    public final int MINIMUM_AGE = 8;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.name_edittext)
    EditText nameEditTExt;

    @BindView(R.id.gender_icon)
    ImageView genderIcon;

    @BindView(R.id.radio_group)
    RadioGroup genderGroup;

    @BindView(R.id.birth_edittext)
    EditText birthEdittext;

    private Calendar myCalendar;
    private Patient patient;
    private AppPreferencesHelper appPreferencesHelper;

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

    private boolean validate() {
        boolean validated = true;
        if (nameEditTExt.getText().toString().isEmpty()) {
            nameEditTExt.setError(getResources().getString(R.string.required_field));
            validated = false;
        }

        if (birthEdittext.getText().toString().isEmpty()) {
            birthEdittext.setError(getResources().getString(R.string.required_field));
            validated = false;
        }
        return validated;
    }

    private void createPatient() {
        patient = new Patient();
        patient.setFirstName(nameEditTExt.getText().toString());
        patient.setBirthDate(birthEdittext.getText().toString());
        Log.i("Patient birthdate", birthEdittext.getText().toString());
        if (genderGroup.getCheckedRadioButtonId() == R.id.male)
            patient.setGender("Male");
        else
            patient.setGender("Female");
    }


    private void initComponents() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        myCalendar = Calendar.getInstance();
        fab.setOnClickListener(v -> {
            if (validate()) {
                createPatient();
                Session session =  new Session(this);
                appPreferencesHelper.saveUserLogged(session.getUserLogged());
                //Intent intent = new Intent();
                //intent.putExtra("patient", patient);
                startActivity(new Intent(PatientRegisterActivity.this, PatientQuiz.class));
                finish();
            }
        });


        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.male)
                genderIcon.setImageResource(R.drawable.x_boy);
            else
                genderIcon.setImageResource(R.drawable.x_girl);
        });

        birthEdittext.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(PatientRegisterActivity.this,
                    (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                birthEdittext.setText(DateUtils.formatDate(myCalendar.getTime().getTime(),
                       "yyyy-MM-dd"));
            }, 2010, 1, 1);
            dialog.show();
        });
    }
}
