package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.dao.PatientDAO;
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

    @BindView(R.id.age_text)
    TextView ageText;

    @BindView(R.id.age_seek)
    SeekBar ageSeek;

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
        return validated;
    }

    private void savePatient() {
        Patient patient = new Patient();
        patient.setName(nameEditTExt.getText().toString());
        //TODO mudar layout
        patient.setBirthDate("");
        //TODO Refatorar Patient
        if (genderGroup.getCheckedRadioButtonId() == R.id.male)
            patient.setGender("Masculino");
        else
            patient.setGender("Feminino");
        patient.set_id("1");
        PatientDAO.getInstance(this).save(patient);
    }

    private void initComponents() {
        fab.setOnClickListener(v -> {
            if (validate()) {
                savePatient();
                startActivity(new Intent(PatientRegisterActivity.this, PatientQuiz.class));
            }
        });

        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.male)
                genderIcon.setImageResource(R.drawable.x_boy);
            else
                genderIcon.setImageResource(R.drawable.x_girl);
        });

        ageSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ageText.setText(String.format("%d anos", progress + MINIMUM_AGE));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
