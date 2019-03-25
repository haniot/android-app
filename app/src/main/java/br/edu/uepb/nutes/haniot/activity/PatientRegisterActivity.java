package br.edu.uepb.nutes.haniot.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * PatientRegisterActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class PatientRegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.name_edittext)
    EditText nameEditTExt;

    @BindView(R.id.last_name_edittext)
    EditText lastNameEditTExt;

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

    /**
     * Validate fields.
     * @return
     */
    private boolean validate() {
        boolean validated = true;
        if (nameEditTExt.getText().toString().isEmpty()) {
            nameEditTExt.setError(getResources().getString(R.string.required_field));
            validated = false;
        }
        if (lastNameEditTExt.getText().toString().isEmpty()) {
            lastNameEditTExt.setError(getResources().getString(R.string.required_field));
            validated = false;
        }
        if (birthEdittext.getText().toString().isEmpty()) {
            birthEdittext.setError(getResources().getString(R.string.required_field));
            validated = false;
        }
        return validated;
    }

    /**
     * Save patient in App Preferences.
     */
    private void savePatient() {
        patient = new Patient();
        patient.setFirstName(nameEditTExt.getText().toString());
        patient.setLastName(lastNameEditTExt.getText().toString());
        patient.setBirthDate(DateUtils.formatDate(myCalendar.getTimeInMillis(), DateUtils.DATE_FORMAT_ISO_8601));
        if (genderGroup.getCheckedRadioButtonId() == R.id.male)
            patient.setGender("male");
        else
            patient.setGender("female");
        appPreferencesHelper.saveLastPatient(patient);
    }

    /**
     * Init components.
     */
    private void initComponents() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        myCalendar = Calendar.getInstance();
        fab.setOnClickListener(v -> {
            if (validate()) {
                savePatient();
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
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            DatePickerDialog dialog = new DatePickerDialog(PatientRegisterActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        birthEdittext.setText(DateUtils.formatDate(myCalendar.getTimeInMillis(), getResources().getString(R.string.date_format)));
                    }, 2010, 1, 1);
            dialog.show();
        });
    }
}