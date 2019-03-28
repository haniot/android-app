package br.edu.uepb.nutes.haniot.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
import br.edu.uepb.nutes.haniot.data.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

/**
 * PatientRegisterActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class PatientRegisterActivity extends AppCompatActivity {
    //TODO implementar edição de paciente

    final private String TAG = "PatientRegisterActivity";
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

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    @BindView(R.id.loading)
    ProgressBar progressBar;

    private Calendar myCalendar;
    private Patient patient;
    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;
    private PatientDAO patientDAO;


    private PhysicalActivityHabit physicalActivityHabits;
    private FeedingHabitsRecord feedingHabitsRecord;
    private MedicalRecord medicalRecord;
    private List<ChronicDisease> chronicDiseases;
    private SleepHabit sleepHabit;
    private List<WeeklyFoodRecord> weeklyFoodRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.patient_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        physicalActivityHabits.setCreatedAt(DateUtils.getCurrentDateISO8601());
//        physicalActivityHabits.set_id("ID");
//        physicalActivityHabits.setId(34234234);
//        physicalActivityHabits.setPatientId("patientID");
//        physicalActivityHabits.setSchoolActivityFreq("Não sei");
//        List<String> strings = new ArrayList<>();
//        strings.add("Futebol");
//        strings.add("Futsal");
//        physicalActivityHabits.setWeeklyActivities(strings);
//        Log.i(TAG, physicalActivityHabits.toJson());
//
//        feedingHabitsRecord.setCreatedAt(DateUtils.getCurrentDateISO8601());
//        feedingHabitsRecord.setBreakfastDailyFrequency("breakfastFrequency");
//        feedingHabitsRecord.setDailyWaterGlasses("AAA");
//        strings.clear();
//        strings.add("AVL");
//        strings.add("Ovo");
//        feedingHabitsRecord.setFoodAllergyIntolerance(strings);
//        feedingHabitsRecord.setSixMonthBreastFeeding("AA");
//        feedingHabitsRecord.set_id("ID");
//        feedingHabitsRecord.setId(231321);
//        feedingHabitsRecord.setPatientId("patientID");
//        WeeklyFoodRecord weeklyFoodRecord = new WeeklyFoodRecord();
//        weeklyFoodRecord.setId(32323);
//        weeklyFoodRecord.setFood("Arroz");
//        weeklyFoodRecord.setSevenDaysFreq("asa");
//        feedingHabitsRecord.setWeeklyFeedingHabits(weeklyFoodRecords);
//        weeklyFoodRecord.setId(32323);
//        weeklyFoodRecord.setFood("Feijão");
//        weeklyFoodRecord.setSevenDaysFreq("asa");
//        feedingHabitsRecord.setWeeklyFeedingHabits(weeklyFoodRecords);
//        Log.i(TAG, feedingHabitsRecord.toJson());


        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boxMessage.setVisibility(View.GONE);
    }

    /**
     * Validate fields.
     *
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
    }

    /**
     * Save patient in App Preferences.
     */
    private void savePatient() {
        patient = new Patient();
        patient.setFirstName(nameEditTExt.getText().toString());
        patient.setLastName(lastNameEditTExt.getText().toString());
        patient.setBirthDate(DateUtils.formatDate(myCalendar.getTimeInMillis(), "yyyy-MM-dd"));
        if (genderGroup.getCheckedRadioButtonId() == R.id.male) patient.setGender("male");
        else patient.setGender("female");
        patient.setPilotId(appPreferencesHelper.getLastPilotStudy().get_id());

        DisposableManager.add(haniotNetRepository
                .savePatient(patient)
                .doOnSubscribe(disposable -> {
                    Log.i(TAG, "Salvando paciente no servidor!");
                    showLoading(true);
                })
                .doAfterTerminate(() -> {
                    showLoading(false);
                    Log.i(TAG, "Salvando paciente no servidor!");
                })
                .subscribe(patient -> {
                    if (patient.get_id() == null) {
                        showMessage(R.string.error_recover_data);
                        return;
                    }
                    patientDAO.save(patient);
                    appPreferencesHelper.saveLastPatient(patient);
                    startActivity(new Intent(PatientRegisterActivity.this, PatientQuizActivity.class));
                }, this::errorHandler));
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
            switch (httpEx.code()) {
                case 409:
                    showMessage(R.string.error_409_patient);
                    break;
                default:
                    showMessage(R.string.error_500);
                    break;
            }
        } else showMessage(R.string.error_500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Loading message,
     *
     * @param enabled boolean
     */
    private void showLoading(final boolean enabled) {
        runOnUiThread(() -> {
            fab.setEnabled(!enabled);
            if (enabled) progressBar.setVisibility(View.VISIBLE);
            else progressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Validate patient.
     */
    View.OnClickListener fabClick = v -> {
        if (validate()) {
            savePatient();
            //finish();
        }
    };

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    private void showMessage(@StringRes int str) {
        String message = getString(str);
        if (message.isEmpty()) message = getString(R.string.error_500);

        messageError.setText(message);
        runOnUiThread(() -> {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            boxMessage.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Init components.
     */
    private void initComponents() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patientDAO = PatientDAO.getInstance(this);
        myCalendar = Calendar.getInstance();
        fab.setOnClickListener(fabClick);

        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.male)
                genderIcon.setImageResource(R.drawable.x_boy);
            else
                genderIcon.setImageResource(R.drawable.x_girl);
        });

        birthEdittext.setOnClickListener(v -> {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}